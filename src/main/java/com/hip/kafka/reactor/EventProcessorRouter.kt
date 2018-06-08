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

      inputStreams.allFluxes().forEach { flux ->
         flux.subscribe(object : Subscriber<Any> { // todo should we use BaseSubscriber?
            private lateinit var s: Subscription

            override fun onSubscribe(s: Subscription) {
               this.s = s
               this.s.request(1)
            }

            override fun onNext(event: Any) {
               eventResponseFlux!!.complete()
               eventResponseFlux = null
               s.request(1)
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