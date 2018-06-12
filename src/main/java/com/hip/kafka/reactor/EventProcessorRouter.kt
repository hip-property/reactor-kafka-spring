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
package com.hip.kafka.reactor

import com.hip.kafka.reactor.processors.EventProcessor
import com.hip.kafka.reactor.streams.InputStreams
import com.hip.kafka.reactor.streams.OutputStreams
import com.hip.utils.log
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import org.springframework.stereotype.Component
import reactor.core.publisher.EmitterProcessor
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink
import reactor.core.scheduler.Schedulers
import javax.annotation.PostConstruct


/**
 *  Responsible for
 *   - routing messages from inbound kafka event stream to Flux's
 *   - creating a replay flux for this event
 *   - subscribing to all outbound flux's
 *   - routing commands and events generated from this event to the temporary flux
 *   - completing the temporary flux
 */
@Component
class EventProcessorRouter(
   private val inputStreams: InputStreams,
   private val outputStreams: OutputStreams,
   @Suppress("unused") val eventProcessor: List<EventProcessor>  // cheap way to ensure that outputStreams are set.  This will die when we do DI
) {

   private var eventResponseFlux: FluxSink<Any>? = null

   @PostConstruct
   fun bindStreams() {
      outputStreams.all().forEach {
         it.subscribe {
            eventResponseFlux!!.next(it)
         }
      }

      // configure the subscribers to request a single message at a time.
      // after this message is consumed push all messages generated on output streams to the kafka publisher
      // this will ensure that the exactly once semantics holds as the inbound message will be withing the same
      // transaction as the outbound message
      inputStreams.allFluxes().forEach { flux ->
         flux.subscribe(object : Subscriber<Any> {
            private lateinit var subscription: Subscription

            override fun onSubscribe(subscription: Subscription) {
               this.subscription = subscription
               this.subscription.request(1)
            }

            override fun onNext(event: Any) {
               // complete the per message flux
               eventResponseFlux!!.complete()
               eventResponseFlux = null
               subscription.request(1)
            }

            override fun onError(t: Throwable) {
               log().error("onError", t)
            }

            override fun onComplete() {
               log().info("onComplete")
            }
         })
      }
   }

   fun handleEvent(event: Any): Flux<Any> {
      val responseEmitter = EmitterProcessor.create<Any>()
      val sink = responseEmitter.sink(FluxSink.OverflowStrategy.BUFFER)
      val flux = responseEmitter.publishOn(Schedulers.immediate())

      if (eventResponseFlux != null) {
         // todo should the handle not get called as each event is being processed?!  I'd have thought so but haven't seen it yet.
         throw RuntimeException("Pretty sure this is going to happen but hasn't yet")
      }

      eventResponseFlux = sink
      val inputSinkForEvent = inputStreams.sink(event::class) as FluxSink<Any>
      log().info("Routing inbound event [$event] to stream")
      inputSinkForEvent.next(event)
      return flux
   }
}
