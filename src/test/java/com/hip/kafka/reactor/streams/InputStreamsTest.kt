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

import com.hip.kafka.reactor.mock.MockFundEvent
import com.hip.kafka.reactor.mock.MockMessage
import com.hip.kafka.reactor.mock.MockOrderEvent
import org.junit.Test
import reactor.test.StepVerifier


class InputStreamsTest {

   @Test
   fun given_singleStreamExists_when_eventAdded_then_fluxProcessesEvent() {
      val inputStreams = InputStreams(setOf(MockFundEvent::class))

      val flux = inputStreams.flux(MockFundEvent::class)
      val sink = inputStreams.sink(MockFundEvent::class)

      StepVerifier.create(flux)
         .then { sink.next(MockFundEvent("1")) }
         .expectNext(MockFundEvent("1"))
   }

   @Test
   fun given_multipleStreamExists_when_eventsAdded_then_fluxProcessesEvents() {
      val inputStreams = InputStreams(setOf(MockFundEvent::class, MockOrderEvent::class))

      StepVerifier.create(inputStreams.flux(MockFundEvent::class))
         .then { inputStreams.sink(MockFundEvent::class).next(MockFundEvent("1")) }
         .expectNext(MockFundEvent("1"))

      StepVerifier.create(inputStreams.flux(MockOrderEvent::class))
         .then { inputStreams.sink(MockOrderEvent::class).next(MockOrderEvent("1")) }
         .expectNext(MockOrderEvent("1"))
   }

   @Test(expected = IllegalArgumentException::class)
   fun when_eventsTypeHasNoAnnotation_then_exceptionThrown() {
      InputStreams(setOf(OopsNoAnnotation::class))
   }

   @Test
   fun given_fluxDeclaresInterface_when_eventIsConcreteClass_then_fluxProcessesEvents() {
      val inputStreams = InputStreams(setOf(MockMessage::class))

      val flux = inputStreams.flux(MockFundEvent::class)
      val sink = inputStreams.sink(MockFundEvent::class)

      StepVerifier.create(flux)
         .then { sink.next(MockFundEvent("1")) }
         .expectNext(MockFundEvent("1"))
   }

}

data class OopsNoAnnotation(
   val id: Int
)

