package com.hip.kafka.reactor.streams

import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import kotlin.reflect.KClass

interface StreamBinder {
   fun <T : Any> consumeFluxOf(type: KClass<T>): Flux<T>
   fun <T : Any> publish(flux: Flux<T>)
}

/**
 * Service locator to expose input and output streams of data to kafka
 *
 * todo replace with direct spring injection of each flux [https://gitlab.com/hipproperty/platform/issues/191]
 */
@Component
class StreamBinderImpl(private val inputStreams: InputStreams, private val outputStreams: OutputStreams): StreamBinder {

   override fun <T : Any> consumeFluxOf(type: KClass<T>): Flux<T> {
      return inputStreams.flux(type)
   }

   override fun <T : Any> publish(flux: Flux<T>) {
      outputStreams.addStream(flux)
   }
}
