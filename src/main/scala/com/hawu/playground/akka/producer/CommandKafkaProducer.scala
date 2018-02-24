package com.hawu.playground.akka.producer

import akka.actor.{Actor, ActorLogging}
import org.apache.kafka.clients.producer.ProducerRecord


case class SendKafkaMessageToTopic(topic: String, message: KafkaMessage)
class CommandKafkaProducer extends Actor with ActorLogging {
  val producer = ProducerFactory()

  def receive = {
    case msg: SendKafkaMessageToTopic =>
      producer.send(new ProducerRecord[String, String](msg.topic, msg.message.serializedMessage))
  }

  override def postStop(): Unit = {
    producer.close()
  }
}
