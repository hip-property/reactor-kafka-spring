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
