package com.hip.kafka.reactor.mock

import com.hip.kafka.KafkaMessage

@KafkaMessage("fundManagerTopic")
data class MockIssueSharesCommand(
   val fundId: String
)
