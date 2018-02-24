package com.hawu.playground.akka.command

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.hawu.playground.akka.consumer.KafkaSerializedMessage
import com.hawu.playground.akka.producer.{KafkaMessage, KafkaMessageBuilder, SendKafkaMessageToTopic}
import com.hawu.playground.akka.utils.Serialization

import scala.util.matching.Regex

class CommandConsumerSideReceiver(persistenceActor: ActorRef, replyProducer: ActorRef) extends Actor with ActorLogging{
  def receive = {
    case msg: KafkaSerializedMessage =>
      val buildedKafkaMessage = KafkaMessageBuilder(msg.message)
      if(buildedKafkaMessage.isDefined) {
        context.actorOf(Props(classOf[CommandConsumerWorker], persistenceActor, replyProducer)) ! buildedKafkaMessage.get
      } else {
        log.error("Cannot build kafka message from {}", msg)
      }
  }
}

class CommandConsumerWorker(persistenceActor: ActorRef, replyProducer: ActorRef) extends Actor with ActorLogging {

  var originalMessage: Option[KafkaMessage] = None

  val commandReplyTopic = context.system.settings.config.getString("playground.command.kafka.replytopic")

  def receive = {
    case message: KafkaMessage =>
      originalMessage = Some(message)

      val deserialized = Serialization(message.serializedMessage)
      if(deserialized.isDefined) {
        persistenceActor.tell(deserialized, replyProducer)
      } else {
        log.error("Cannot deserialize message {}", message.serializedMessage)
      }
      context.stop(self)

    case messge: HttpCommandResponse =>
      originalMessage.map(om => {
        replyProducer ! SendKafkaMessageToTopic(commandReplyTopic, KafkaMessage(System.currentTimeMillis(), om.replyToken, Serialization(messge)))
      })
      context.stop(self)
  }
}

object CommandConsumePatterMaker {
  def apply[T](implicit m: scala.reflect.ClassTag[T]): Regex = {
    (".*" + m.getClass.getSimpleName + ".*").r
  }
}


