package com.hawu.playground.akka.tests.dummy

import akka.actor.{Actor, ActorLogging}
import com.hawu.playground.akka.command._
import com.hawu.playground.akka.producer.KafkaMessage
import com.hawu.playground.akka.utils.Serialization

class SuccessCommandController(testMessages: List[String]) extends Actor with ActorLogging {

  override def receive: Receive = {
    case GetMessagesForGroup(g) =>
      val sndr = sender()
      val msg = GotMessages(g, testMessages)
      Serialization(msg).map(serialized => {
        sndr ! KafkaMessage(0l, "", serialized, msg.getClass.getTypeName)
      })

    case msg: GetAllMessages =>
      val sndr = sender()
      val msg = GotMessages("", testMessages)
      Serialization(msg).map(serialized => {
        sndr ! KafkaMessage(0l, "", serialized, msg.getClass.getTypeName)
      })

    case CreateGroup(groupId) =>
      val sndr = sender()
      val msg = GroupCreated(groupId)
      Serialization(msg).map(serialized => {
        sndr ! KafkaMessage(0l, "", serialized, msg.getClass.getTypeName)
      })

    case DeleteGroupById(groupId) =>
      val sndr = sender()
      val msg = GroupByIdDeleted(groupId)
      Serialization(msg).map(serialized => {
        sndr ! KafkaMessage(0l, "", serialized, msg.getClass.getTypeName)
      })

    case AssignMessageToGroup(group, message) =>
      val sndr = sender()
      val msg = AssignMessageToGroupCompleted(group, message)
      Serialization(msg).map(serialized => {
        sndr ! KafkaMessage(0l, "", serialized, msg.getClass.getTypeName)
      })
  }
}
