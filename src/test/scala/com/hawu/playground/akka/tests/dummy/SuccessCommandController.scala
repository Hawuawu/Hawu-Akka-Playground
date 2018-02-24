package com.hawu.playground.akka.tests.dummy

import akka.actor.{Actor, ActorLogging}
import com.hawu.playground.akka.command._
import com.hawu.playground.akka.producer.KafkaMessage
import com.hawu.playground.akka.utils.Serialization

class SuccessCommandController(testMessages: List[String]) extends Actor with ActorLogging{
  override def receive: Receive = {
    case GetMessagesForGroup(g) =>
      val msg = GotMessages(g, testMessages)
      Serialization(msg).map(serialized => {
        sender ! KafkaMessage(0l, "", serialized, msg.getClass.getTypeName)
      })

    case msg: GetAllMessages =>
      val msg = GotMessages("", testMessages)
      Serialization(msg).map(serialized => {
        sender ! KafkaMessage(0l, "", serialized, msg.getClass.getTypeName)
      })


    case CreateGroup(groupId) =>
      val msg = GroupCreated(groupId)
      Serialization(msg).map(serialized => {
        sender ! KafkaMessage(0l, "", serialized, msg.getClass.getTypeName)
      })

    case DeleteGroupById(groupId) =>
      val msg = GroupByIdDeleted(groupId)
      Serialization(msg).map(serialized => {
        sender ! KafkaMessage(0l, "", serialized, msg.getClass.getTypeName)
      })

    case AssignMessageToGroup(group, message) =>
      val msg = AssignMessageToGroupCompleted(group, message)
      Serialization(msg).map(serialized => {
        sender ! KafkaMessage(0l, "", serialized, msg.getClass.getTypeName)
      })
  }
}
