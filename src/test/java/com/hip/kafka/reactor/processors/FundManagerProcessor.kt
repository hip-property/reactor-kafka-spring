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

