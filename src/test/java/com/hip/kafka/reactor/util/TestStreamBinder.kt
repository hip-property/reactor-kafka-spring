/*-
 * =========================================================BeginLicense
 * Reactive Kafka
 * .
 * Copyright (C) 2018 HiP Property
 * .
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ===========================================================EndLicense
 */
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
