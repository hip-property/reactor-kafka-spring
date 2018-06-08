package com.hip.kafka.reactor.processors

/**
 * Classes that consume messages from the reactor spring should implement this.  Eventually this will allow
 * spring to find the classes and get meta data from the input and output annotations.  Right now this ensures
 * that the service locator [com.hip.kafka.reactor.streams.StreamBinder] publish method is called before
 * [com.hip.kafka.reactor.EventProcessorRouter] uses it.
 */
interface EventProcessor
