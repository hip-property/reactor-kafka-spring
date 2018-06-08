package com.hip.kafka.reactor.mock

import com.hip.kafka.KafkaMessage

@KafkaMessage(topic = "fundEvents")
data class MockFundEvent(
   val id: String
) : MockMessage

@KafkaMessage(topic = "fundEvents")
interface MockMessage
