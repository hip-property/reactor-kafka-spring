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

import com.hip.kafka.reactor.EventProcessorRouter
import com.hip.kafka.reactor.mock.MockFundEvent
import com.hip.kafka.reactor.mock.MockOrderEvent
import com.hip.kafka.reactor.streams.InputStreams
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication(scanBasePackageClasses = [(EventProcessorRouter::class)])
class ReactorSpringSampleApp {

   @Bean
   fun inputStreams(): InputStreams {
      return InputStreams(setOf(MockFundEvent::class, MockOrderEvent::class))
   }
}

fun main(args: Array<String>) {
   runApplication<ReactorSpringSampleApp>(*args)
}

