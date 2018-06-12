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

/**
 * Classes that consume messages from the reactor spring should implement this.  Eventually this will allow
 * spring to find the classes and get meta data from the input and output annotations.  Right now this ensures
 * that the service locator [com.hip.kafka.reactor.streams.StreamBinder] publish method is called before
 * [com.hip.kafka.reactor.EventProcessorRouter] uses it.
 */
interface EventProcessor
