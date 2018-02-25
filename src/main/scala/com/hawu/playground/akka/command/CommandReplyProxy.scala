package com.hawu.playground.akka.command

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.hawu.playground.akka.producer.KafkaMessage
//import com.hawu.playground.akka.utils.Serialization // UNUSED

import scala.concurrent.duration._


case class RegisterKafkaMessageForReply(message: KafkaMessage)
case class GotReplyForKafkaMessage(reply: KafkaMessage)

case class ReplyKafkaMessageRegistryRow(message: KafkaMessage, timeouter: ActorRef, replyTo: ActorRef)
class CommandReplyProxy extends Actor with ActorLogging {
  var registry: List[ReplyKafkaMessageRegistryRow] = List()

  def receive = {
    case msg: RegisterKafkaMessageForReply =>
      registry =
        ReplyKafkaMessageRegistryRow(
        msg.message,
        context.actorOf(Props(classOf[ReplyTimeouter], self, msg.message.replyToken)),
          sender
        ) :: registry

    case msg: GotReplyForKafkaMessage =>
      registry = registry.filter(f => if (f.message.replyToken == msg.reply.replyToken) {
        f.replyTo ! msg.reply
        context.stop(f.timeouter)
        false
      } else {
        true
      })

    case CommandTimeouted =>
      registry = registry.filter(f => if (f.timeouter == sender) {
        f.replyTo ! CommandTimeouted
        context.stop(f.timeouter)
        false
      } else {
        true
      })
  }
}

class ReplyTimeouter(parent: ActorRef, hash: String) extends Actor with ActorLogging {
  context.setReceiveTimeout(10 seconds)

   def receive = {
    case akka.actor.ReceiveTimeout =>
      log.debug("No longer waiting for reply {}", hash)
      parent ! CommandTimeouted
  }
}
