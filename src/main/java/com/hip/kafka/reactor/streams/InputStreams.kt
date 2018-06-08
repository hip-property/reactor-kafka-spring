package com.hip.kafka.reactor.streams

import com.hip.kafka.KafkaMessage
import com.hip.utils.log
import reactor.core.publisher.EmitterProcessor
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink
import reactor.core.scheduler.Schedulers
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

/**
 * Responsible for creating a flux's for each kafka input event stream and helper methods around that
 *
 * Note: open for testing
 */
open class InputStreams(eventClasses: Set<KClass<out Any>>) {
   private val inputStreams: Map<KClass<*>, Pair<Flux<Any>, FluxSink<Any>>> =
      eventClasses.map { eventClass ->
         val emitter = EmitterProcessor.create<Any>()
         val sink = emitter.sink(FluxSink.OverflowStrategy.BUFFER)
         val flux = emitter.publishOn(Schedulers.immediate())

         log().info("created flux for class [${eventClass.simpleName}] topic [${eventClass.topic()}]")
         eventClass to (flux to sink)
      }.toMap()

   fun <T : Any> sink(type: KClass<T>): FluxSink<T> {
      val mappedType = getMappedType(type)
      @Suppress("UNCHECKED_CAST") // nothing we can do
      return inputStreams[mappedType]?.second as FluxSink<T>
   }

   fun <T : Any> flux(type: KClass<T>): Flux<T> {
      val mappedType = getMappedType(type)
      @Suppress("UNCHECKED_CAST") // nothing we can do
      return inputStreams[mappedType]!!.first as Flux<T>
   }

   private fun <T : Any> getMappedType(type: KClass<T>): KClass<out T> {
      if (inputStreams.containsKey(type)) return type

      val matchingTypes = type.supertypes.filter {
         inputStreams.containsKey(it.classifier)
      }
      if (matchingTypes.size > 1) error("$type is ambiguous, and has multiple input fluxes declared")
      if (matchingTypes.isEmpty()) error("No input mapping declared for $type or any of it's supertypes")
      return matchingTypes.first().classifier as KClass<out T>
   }

   fun allFluxes(): List<Flux<Any>> {
      return inputStreams.map { it.value.first }
   }

   fun allTopics(): Set<String> {
      return inputStreams.map {
         it.key.topic()
      }.toSet()
   }

   private fun KClass<*>.topic(): String {
      return (this.findAnnotation<KafkaMessage>()
         ?: throw IllegalArgumentException("Type [$this] should have a @KafkaMessage annotation")).topic
   }
}
