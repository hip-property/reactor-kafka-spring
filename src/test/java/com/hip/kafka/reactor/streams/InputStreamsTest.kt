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

