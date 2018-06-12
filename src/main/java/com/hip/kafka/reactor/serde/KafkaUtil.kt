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
