package com.hip.kafka.reactor.streams

import org.springframework.stereotype.Component
import reactor.core.publisher.Flux

/**
 * Output streams that will be written to kafka
 */
@Component
class OutputStreams {
   private val outputStreams: MutableList<Flux<*>> = mutableListOf()

   fun <T> addStream(flux: Flux<T>) {
      outputStreams.add(flux)
   }

   fun all(): List<Flux<*>> {
      return outputStreams.toList()
   }
}
