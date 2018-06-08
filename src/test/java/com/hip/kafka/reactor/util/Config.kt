package com.hip.kafka.reactor.util

import com.hip.kafka.reactor.ReactorKafkaSpringReceiverConfig
import com.hip.kafka.reactor.ReactorKafkaSpringSenderConfig

/**
 * todo kill this and use emmbeddedKAfka/zookeeper test
 */

fun getSenderConfig() : ReactorKafkaSpringSenderConfig {
   val reactorSpringConfig = ReactorKafkaSpringSenderConfig()
   reactorSpringConfig.kafkaServerAddress = "localhost:9092"
   reactorSpringConfig.clientId = "testClientId"
   return reactorSpringConfig
}

fun getReceiverConfig() : ReactorKafkaSpringReceiverConfig {
   val reactorSpringConfig = ReactorKafkaSpringReceiverConfig()
   reactorSpringConfig.kafkaServerAddress = "localhost:9092"
   reactorSpringConfig.group = "group"
   reactorSpringConfig.clientId = "testClientId"
   return reactorSpringConfig
}
