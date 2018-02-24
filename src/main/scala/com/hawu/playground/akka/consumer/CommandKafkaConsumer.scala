package com.hawu.playground.akka.consumer

import akka.actor.{Actor, ActorLogging, ActorRef}
import com.hawu.playground.akka.utils.SafeConfigLoader

import scala.collection.JavaConverters._


case object ListenForNewMessage
case class KafkaSerializedMessage(message: String)
class CommandKafkaConsumer(commandReceiver: ActorRef, replyReceiver: ActorRef) extends Actor with ActorLogging {

  val commandReplyTopic = context.system.settings.config.getString("playground.command.kafka.replytopic")
  val commandTopic = context.system.settings.config.getString("playground.command.kafka.topic")

  val consumer = ConsumerFactory()
  consumer.subscribe(List(commandReplyTopic, commandTopic).asJava)

  val pollTime = SafeConfigLoader[Long] (
    context.system.settings.config,
    "playground.command.kafka.consumer.polltime",
    (config, path) => {
      config.getLong(path)
    }
  )

  self ! ListenForNewMessage
  def receive = {
    case ListenForNewMessage =>
      try {
        val messages = consumer.poll(pollTime.getOrElse(1000)) //I hate this, but its blocking operation (polling system for kafka)
        messages.iterator.asScala.foreach(item => {
          item.topic match {
            case `commandReplyTopic` => replyReceiver ! KafkaSerializedMessage(item.value())
            case `commandTopic` => commandReceiver ! KafkaSerializedMessage(item.value())
            case _ => log.error("Got message with unknown topic")
          }
        })
      } catch {
        case t: Throwable =>
          log.debug("Got exception while polling messages {}", t)
      }
      self ! ListenForNewMessage
  }

  override def postStop(): Unit = {
    consumer.close
  }
}
