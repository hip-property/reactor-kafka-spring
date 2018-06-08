package com.hip.kafka.reactor.serde

import com.hip.kafka.KafkaMessage
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

/**
 * Utility methods around reactor kafka
 */

fun getTopic(any: Any): String {
   return getTopic(any::class)
}

fun getTopic(clazz: KClass<*>): String {
   return clazz.topic()
}

/**
 * private to keep KCLass namespace clean as this is quite specific to classes with [KafkaMessage] annotation
 */
private fun KClass<*>.topic(): String {
   return (this.findAnnotation<KafkaMessage>()
      ?: throw IllegalArgumentException("Type [$this] should have a @KafkaMessage annotation")).topic
}
