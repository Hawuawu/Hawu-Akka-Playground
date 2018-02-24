package com.hawu.playground.akka.command

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.hawu.playground.akka.consumer.KafkaSerializedMessage
import com.hawu.playground.akka.producer.{KafkaMessage, KafkaMessageBuilder}

class RepliesReceiver(commandReplyProxy: ActorRef) extends Actor with ActorLogging {
  def receive = {
    case msg: KafkaSerializedMessage =>
      val buildedKafkaMessage = KafkaMessageBuilder(msg.message)
      if(buildedKafkaMessage.isDefined) {
        context.actorOf(Props(classOf[ReplyReceiveWorker], commandReplyProxy, buildedKafkaMessage.get)) ! buildedKafkaMessage.get
      } else {
        log.error("Cannot build kafka message from {}", msg)
      }
  }
}

class ReplyReceiveWorker(replyProxy: ActorRef, message: KafkaMessage) extends Actor with ActorLogging {
  def receive = {
    case msg: KafkaMessage =>
      replyProxy ! GotReplyForKafkaMessage(message)
      context.stop(self)
  }
}

