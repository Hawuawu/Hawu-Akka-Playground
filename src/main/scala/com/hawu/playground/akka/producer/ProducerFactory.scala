package com.hawu.playground.akka.producer

import com.hawu.playground.akka.utils.ConfConverter
import com.typesafe.config.ConfigFactory
import org.apache.kafka.clients.producer.KafkaProducer

object ProducerFactory {

  def apply(): KafkaProducer[String, String] = {
    val kafkaConf = ConfigFactory.load().getConfig("playground.kafka.producer")
    new KafkaProducer[String, String](ConfConverter.toProperties(kafkaConf))
  }
}
