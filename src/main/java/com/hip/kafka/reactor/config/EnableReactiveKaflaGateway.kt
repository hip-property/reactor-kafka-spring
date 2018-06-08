package com.hip.kafka.reactor.config

import com.hip.kafka.reactor.ReplayAwareKafkaConsumer
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import
import org.springframework.kafka.annotation.EnableKafka

@EnableKafka
@Import(ReactiveKafkaGatewayConfig::class)
annotation class EnableReactiveKafkaGateway

@ComponentScan(basePackageClasses = [(ReplayAwareKafkaConsumer::class)])
class ReactiveKafkaGatewayConfig


