/*
 * Copyright (c) 2016-2017 Pivotal Software Inc, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hip.kafka.reactor.util

import com.hip.kafka.reactor.mock.MockFundEvent
import com.hip.kafka.reactor.mock.MockOrderEvent
import com.hip.kafka.reactor.serde.getTopic
import com.hip.utils.log
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import reactor.core.publisher.Flux
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderRecord
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


class SampleProducer {

   @Throws(InterruptedException::class)
   fun sendMessages(count: Int, latch: CountDownLatch) {
      val sender = KafkaSender.create(
         getSenderConfig().senderOptions()
            .producerProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
      )

      sender.send(Flux.range(1, count)
         .map { i ->

            val value: Any = when(i.rem(2) == 0) {
               true -> MockFundEvent("Fund_Message_" + i!!)
               false -> MockOrderEvent("Order_Message_" + i!!)
            }

            val producerRecord = ProducerRecord(getTopic(value), "$i", value)
            SenderRecord.create(producerRecord, i)
         })
         .doOnError { e -> log().error("Send failed", e) }
         .subscribe { r ->
            val metadata = r.recordMetadata()
            log().info("Message ${r.correlationMetadata()} sent sent, " +
               "topic-partition=${metadata.topic()}-${metadata.partition()} offset=${metadata.offset()} " +
               "timestamp=${metadata.timestamp()}")
            latch.countDown()
         }
   }
}

fun main(args: Array<String>) {
   val count = 20
   val latch = CountDownLatch(count)
   val producer = SampleProducer()
   producer.sendMessages(count, latch)
   latch.await(10, TimeUnit.SECONDS)
}
