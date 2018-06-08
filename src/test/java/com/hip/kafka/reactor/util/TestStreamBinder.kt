package com.hip.kafka.reactor.util

import com.hip.kafka.reactor.streams.StreamBinder
import reactor.core.publisher.Flux
import reactor.test.publisher.TestPublisher
import kotlin.reflect.KClass

class TestStreamBinder : StreamBinder {
   val publishingFluxes = mutableListOf<Flux<*>>()
   val consumingFluxes: MutableMap<KClass<out Any>, TestPublisher<out Any>> = mutableMapOf()
   override fun <T : Any> consumeFluxOf(type: KClass<T>): Flux<T> {
      return getOrCreateTestPublisher(type).flux()
   }

   inline fun <reified T : Any> testPublisher(): TestPublisher<T> {
      return getOrCreateTestPublisher(T::class)
   }

   fun <T : Any> getOrCreateTestPublisher(type: KClass<T>): TestPublisher<T> {
      return consumingFluxes.getOrPut(type, { TestPublisher.create<T>() }) as TestPublisher<T>
   }

   override fun <T : Any> publish(flux: Flux<T>) {
      publishingFluxes.add(flux)
   }

}
