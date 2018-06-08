package com.hip.kafka.reactor

import com.hip.utils.log
import org.junit.Test
import reactor.core.publisher.EmitterProcessor
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers


class ReactorTest {

//   @Test
//   fun foo() {
//
//      val recordEmitter = EmitterProcessor.create<String>()
//      val recordSubmission = recordEmitter.sink(FluxSink.OverflowStrategy.BUFFER)
//      val publishOn = recordEmitter.publishOn(Schedulers.parallel())
//
//
//
//      val value1: BaseSubscriber<String> = object : BaseSubscriber<String>() {
//         protected override fun hookOnSubscribe(subscription: Subscription) {
//
//            request(1)
//         }
//
//         protected override fun hookOnNext(value: String) {
//            request(1)
//         }
//      }
//
//      publishOn.map{}
//      .subscribe(object : Subscriber<String>{
//         override fun onComplete() {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//         }
//
//         override fun onSubscribe(p0: Subscription?) {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//         }
//
//         override fun onNext(p0: String?) {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//         }
//
//         override fun onError(p0: Throwable?) {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//         }
//      })
//
//
//   }

   @Test
   fun foo() {


      val emitter = EmitterProcessor.create<String>()
      val fluxSink = emitter.sink(FluxSink.OverflowStrategy.BUFFER)
      val flux = emitter.publishOn(Schedulers.immediate())

      flux.concatMap { Mono.just(it) }
         .subscribe { t: String? -> }


   }

   @Test
   fun doTwoPiecesOfWorkThen() {
      val flux: Flux<String> = Flux.just("1", "2")

      flux.flatMap {
         val mono1 = Mono.just("mono1 $it")
         val mono2 = Mono.just(it)

         mono1.zipWith(mono2)
      }.subscribe {
         log().info("mono1[${it.t1}] mono2[${it.t2}]")
      }
   }

}

