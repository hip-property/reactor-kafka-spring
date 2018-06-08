package com.hip.kafka.reactor.serde

import com.hip.json.jackson
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer

/*
 * Just use the Spring version but set the object mapper.   Needs to be a class so kafka can instantiate
 */
class KotlinJsonSerializer: JsonSerializer<Any>(jackson())
class KotlinJsonDeserializer: JsonDeserializer<Any>(jackson())

