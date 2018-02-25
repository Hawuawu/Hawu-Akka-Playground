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
      // INFO Nil is static value of List() save allocation
      sender ! GotMessages(g, Nil)

    case msf: GetAllMessages =>
      sender ! GotMessages("", Nil)

    case CreateGroup(groupId) =>
      // INFO extract sender's copy to local scope when calling it from withing map
      val sndr = sender()
      val msg = CannotCreateGroup(groupId, cannotCreateGroupReason)
      Serialization(msg).map(serialized => {
        sndr ! KafkaMessage(0l, "", serialized, msg.getClass.getTypeName)
      })

    case DeleteGroupById(groupId) =>
      val sndr = sender()
      val msg = CannotDeleteGroupById(groupId, cannotDeleteGroupReason)
      Serialization(msg).map(serialized => {
        sndr ! KafkaMessage(0l, "", serialized, msg.getClass.getTypeName)
      })

    case AssignMessageToGroup(group, message) =>
      val sndr = sender()
      val msg = AssignMessageToGroupFailed(group, message, cannotAssignMessageToGroupReason)
      Serialization(msg).map(serialized => {
        sndr ! KafkaMessage(0l, "", serialized, msg.getClass.getTypeName)
      })
  }
}
