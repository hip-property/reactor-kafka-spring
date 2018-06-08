package com.hip.kafka.reactor

import com.hip.json.jackson
import com.hip.kafka.reactor.serde.KotlinJsonDeserializer
import com.hip.kafka.reactor.serde.KotlinJsonSerializer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.support.serializer.JsonDeserializer
import reactor.kafka.receiver.ReceiverOptions
import reactor.kafka.sender.SenderOptions
import java.util.*

@Configuration
class ReactorKafkaSpringReceiverConfig {

   @Autowired
   @Value("\${kafka.address:localhost:9092}")
   lateinit var kafkaServerAddress: String

   @Autowired
   @Value("\${kafka.group}")
   lateinit var group: String

   @Autowired
   @Value("\${kafka.clientId}")
   lateinit var clientId: String

   @Bean
   fun jsonDeserializer(): JsonDeserializer<Any> {
      // See jsonSerializer() re use of jackson()
      val deserializer = JsonDeserializer(Any::class.java, jackson())
      deserializer.addTrustedPackages("*")
      return deserializer
   }

   @Bean
   fun receiverOptions(): ReceiverOptions<String, Any> {
      val props = HashMap<String, Any>()
      props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaServerAddress
      props[ConsumerConfig.CLIENT_ID_CONFIG] = clientId
      props[ConsumerConfig.GROUP_ID_CONFIG] = group
      props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
      props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = KotlinJsonDeserializer::class.java
      props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
      props[ConsumerConfig.ISOLATION_LEVEL_CONFIG] = "read_committed"
      props[JsonDeserializer.TRUSTED_PACKAGES] = "*"

      return ReceiverOptions.create(props)
   }
}

/**
 * Uniquely identifies a consumer.  For now just use client id.  Eventually we will
 * want to concat the group id.
 */
fun ReceiverOptions<*, *>.identifier(): String {
   return "${consumerProperties()[ConsumerConfig.CLIENT_ID_CONFIG]}"
}

@Configuration
class ReactorKafkaSpringSenderConfig {

   @Autowired
   @Value("\${kafka.address:localhost:9092}")
   lateinit var kafkaServerAddress: String

   @Autowired
   @Value("\${kafka.transaction}")
   var kafkaTransaction: String? = null // if null no transaction

   @Autowired
   @Value("\${kafka.clientId}")
   lateinit var clientId: String

   @Bean
   fun jsonDeserializer(): JsonDeserializer<Any> {
      // See jsonSerializer() re use of jackson()
      val deserializer = JsonDeserializer(Any::class.java, jackson())
      deserializer.addTrustedPackages("*")
      return deserializer
   }

   @Bean
   fun senderOptions(): SenderOptions<String, Any> {
      val props = HashMap<String, Any>()
      props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaServerAddress
      props[ProducerConfig.CLIENT_ID_CONFIG] = clientId!!
      props[ProducerConfig.ACKS_CONFIG] = "all"
      props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
      props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = KotlinJsonSerializer::class.java
      if (kafkaTransaction != null) {
         props[ProducerConfig.TRANSACTIONAL_ID_CONFIG] = kafkaTransaction!!
      }
      return SenderOptions.create<String, Any>(props)
   }
}
