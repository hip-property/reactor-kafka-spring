package com.hip.kafka.reactor.util

import com.hip.kafka.reactor.EventProcessorRouter
import com.hip.kafka.reactor.mock.MockFundEvent
import com.hip.kafka.reactor.mock.MockOrderEvent
import com.hip.kafka.reactor.streams.InputStreams
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication(scanBasePackageClasses = [(EventProcessorRouter::class)])
class ReactorSpringSampleApp {

   @Bean
   fun inputStreams(): InputStreams {
      return InputStreams(setOf(MockFundEvent::class, MockOrderEvent::class))
   }
}

fun main(args: Array<String>) {
   runApplication<ReactorSpringSampleApp>(*args)
}

