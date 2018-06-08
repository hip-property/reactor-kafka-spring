package com.hip.kafka.reactor

import com.hip.kafka.reactor.serde.getTopic
import com.hip.kafka.reactor.streams.InputStreams
import com.hip.reactive.component1
import com.hip.reactive.component2
import com.hip.utils.log
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.TopicPartition
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverOptions
import reactor.kafka.receiver.ReceiverPartition
import reactor.kafka.sender.*
import reactor.util.function.Tuple2
import java.util.concurrent.ConcurrentHashMap
import javax.annotation.PostConstruct

/**
 * Responsible for consuming multiple kafka event streams and publishing events back out to kafka.  Uses read exactly once
 * semantics.
 *
 * On startup:
 *   - captures the last offset that was consumed
 *   - seeks back to the start
 *   - replays all messages
 *   - if a message is being replayed and side effects (publishing messages to kafka) will not be repeated
 */
@Component
class ReplayAwareKafkaConsumer(
   private val eventProcessorRouter: EventProcessorRouter,
   private val receiverOptions: ReceiverOptions<String, Any>,
   private val senderOptions: SenderOptions<String, Any>,
   private val inputStreams: InputStreams
) {

   private val initialOffsets = ConcurrentHashMap<TopicPartition, Long>()

   @PostConstruct
   fun consumeMessages() {
      val sender = KafkaSender.create<String, Any>(senderOptions)
      val transactionManager = sender.transactionManager()
      val receiver = createReceiver(receiverOptions, transactionManager)

      receiver.concatMap { consumerRecordsFlux ->
         sender.send(getRecordsToSend(consumerRecordsFlux))
            .concatWith(transactionManager.commit<SenderResult<String>?>())
      }.subscribe()
   }

   private fun createReceiver(receiverOptions: ReceiverOptions<String, Any>, transactionManager: TransactionManager?): Flux<Flux<ConsumerRecord<String, Any>>> {
      return KafkaReceiver.create(receiverOptions.subscription(inputStreams.allTopics())
         .addRevokeListener({ partitions -> log().warn("onPartitionsRevoked {}", partitions) })
         .addAssignListener({ partitions: Collection<ReceiverPartition> -> partitions.forEach { captureCurrentOffsetAndSeekToBeginning(it) } })
      ).receiveExactlyOnce(transactionManager)
   }

   private fun getRecordsToSend(consumerRecordsFlux: Flux<ConsumerRecord<String, Any>>): Flux<SenderRecord<String, Any, String>?>? {
      return consumerRecordsFlux
         .concatMap { consumerRecord -> processEvent(consumerRecord) }
         .filter { (record, consumerRecord) -> isReplaying(consumerRecord, record) }
         .map { (record, consumerRecord) -> createSenderRecord(record, consumerRecord) }
   }

   private fun createSenderRecord(record: Any, consumerRecord: ConsumerRecord<String, Any>): SenderRecord<String, Any, String>? {
      log().info("Sending record $record")
      return SenderRecord.create(getTopic(record), consumerRecord.partition(), null, consumerRecord.key(), record, consumerRecord.key())
   }

   private fun isReplaying(consumerRecord: ConsumerRecord<String, Any>, record: Any): Boolean {
      val isReplaying = consumerRecord.offset() > (initialOffsets[TopicPartition(consumerRecord.topic(), consumerRecord.partition())]!! - 1)
      if (!isReplaying) {
         log().info("REPLAYING Not sending offset[${consumerRecord.offset()}] message [$record]")
      }
      return isReplaying
   }

   private fun processEvent(consumerRecord: ConsumerRecord<String, Any>): Flux<Tuple2<Any, ConsumerRecord<String, Any>>>? {
      val handleEvent = eventProcessorRouter.handleEvent(consumerRecord.value())
      return handleEvent.zipWith(Mono.just(consumerRecord))
   }

   private fun captureCurrentOffsetAndSeekToBeginning(partition: ReceiverPartition) {
      val topicPartition = partition.topicPartition()
      val initialOffset = partition.position()
      initialOffsets[topicPartition] = initialOffset
      log().info("Resetting starting offset to start for topic[${topicPartition.topic()}] partition[${topicPartition.partition()}] from offset [$initialOffset]")
      partition.seekToBeginning()
   }
}
