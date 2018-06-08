package com.hip.kafka.reactor.util


import com.hip.kafka.reactor.mock.MockIssueSharesCommand
import com.hip.kafka.reactor.serde.getTopic
import com.hip.utils.log
import org.apache.kafka.clients.consumer.ConsumerConfig
import reactor.core.Disposable
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverPartition
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class OutTopicConsumer {

   fun consumeMessages(topic: String): Disposable {
      val options = getReceiverConfig().receiverOptions()
         .consumerProperty(ConsumerConfig.GROUP_ID_CONFIG, "sample-group2")
         .subscription(setOf(topic))
         .addAssignListener({ partitions: Collection<ReceiverPartition> -> partitions.forEach { receiverPartition -> println(receiverPartition.position()) } })
         .addRevokeListener({ partitions -> log().info("onPartitionsRevoked {}", partitions) })

      val dateFormat = SimpleDateFormat("HH:mm:ss:SSS z dd MMM yyyy")

      val kafkaFlux = KafkaReceiver.create<String, Any>(options).receive()
      return kafkaFlux.subscribe { record ->
         val offset = record.receiverOffset()
         System.out.printf("Received message: topic-partition=%s offset=%d timestamp=%s key=%s value=%s\n",
            offset.topicPartition(),
            offset.offset(),
            dateFormat.format(Date(record.timestamp())),
            record.key(),
            record.value())
         offset.acknowledge()
      }
   }
}

fun main(args: Array<String>) {
   val replayAwareConsumer = OutTopicConsumer()
   val disposable = replayAwareConsumer.consumeMessages(getTopic(MockIssueSharesCommand::class))
   TimeUnit.HOURS.sleep(1)
   disposable.dispose()
}
