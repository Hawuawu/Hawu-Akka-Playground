package com.hawu.playground.akka.tests.dummy

import akka.actor.{Actor, ActorLogging}
import com.hawu.playground.akka.command._
import com.hawu.playground.akka.producer.KafkaMessage
import com.hawu.playground.akka.utils.Serialization

class FailureCommandController(
                                cannotCreateGroupReason: String,
                                cannotDeleteGroupReason: String,
                                cannotAssignMessageToGroupReason: String) extends Actor with ActorLogging{
  override def receive: Receive = {
    case GetMessagesForGroup(g) =>
      sender ! GotMessages(g, List())

    case msf: GetAllMessages =>
      sender ! GotMessages("", List())

    case CreateGroup(groupId) =>
      val msg = CannotCreateGroup(groupId, cannotCreateGroupReason)
      Serialization(msg).map(serialized => {
        sender ! KafkaMessage(0l, "", serialized, msg.getClass.getTypeName)
      })

    case DeleteGroupById(groupId) =>
      val msg = CannotDeleteGroupById(groupId, cannotDeleteGroupReason)
      Serialization(msg).map(serialized => {
        sender ! KafkaMessage(0l, "", serialized, msg.getClass.getTypeName)
      })

    case AssignMessageToGroup(group, message) =>
      val msg = AssignMessageToGroupFailed(group, message, cannotAssignMessageToGroupReason)
      Serialization(msg).map(serialized => {
        sender ! KafkaMessage(0l, "", serialized, msg.getClass.getTypeName)
      })
  }
}
