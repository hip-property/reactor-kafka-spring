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
package com.hip.kafka.reactor.processors

import com.hip.kafka.reactor.mock.MockIssueSharesCommand
import com.hip.kafka.reactor.mock.MockFundEvent
import com.hip.kafka.reactor.mock.MockOrderEvent
import com.hip.kafka.reactor.streams.StreamBinder
import com.hip.utils.log
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux


@Component
class FundManagerProcessor(streamBinder: StreamBinder): EventProcessor {

   private final val commandsFlux: Flux<MockIssueSharesCommand>

   private var count: Int = 0

   init {
      log().info("setting up FundManagerProcessor")
      commandsFlux = streamBinder.consumeFluxOf(MockFundEvent::class)
         .filter { count++.rem(2) == 0 }
         .map {
            log().info("issueShares for fund called $it - issuing shares")
            MockIssueSharesCommand("${it.id} create shares")
         }

      streamBinder.publish(commandsFlux)
   }
}

@Component
class OrderManagerProcessor(streamBinder: StreamBinder): EventProcessor {

   private final val commandsFlux: Flux<MockIssueSharesCommand>

   private var count: Int = 0

   init {
      log().info("setting up OrderManagerProcessor")
      commandsFlux = streamBinder.consumeFluxOf(MockOrderEvent::class)
         .filter { count++.rem(2) == 0 }
         .map {
            log().info("issueShares for order called $it - issuing shares")
            MockIssueSharesCommand("${it.id} create shares")
         }

      streamBinder.publish(commandsFlux)
   }
}

