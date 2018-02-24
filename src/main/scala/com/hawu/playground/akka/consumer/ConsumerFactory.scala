package com.hawu.playground.akka.consumer


import com.hawu.playground.akka.utils.ConfConverter
import com.typesafe.config.ConfigFactory
import org.apache.kafka.clients.consumer.KafkaConsumer

object ConsumerFactory {
  def apply(): KafkaConsumer[String, String] = {
    val kafkaConf = ConfigFactory.load().getConfig("playground.kafka.consumer")
    new KafkaConsumer[String, String](ConfConverter.toProperties(kafkaConf))
  }
}
