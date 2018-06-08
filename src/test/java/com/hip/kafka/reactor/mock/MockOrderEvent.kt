package com.hip.kafka.reactor.mock

import com.hip.kafka.KafkaMessage

@KafkaMessage(topic = "orderEvents")
data class MockOrderEvent(
   val id: String
)
