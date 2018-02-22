package com.hawu.playground.akka.tests.dummy

import akka.actor.{Actor, ActorLogging}
import com.hawu.playground.akka.command._

class FailureCommandController(
                                cannotCreateGroupReason: String,
                                cannotDeleteGroupReason: String,
                                cannotAssignMessageToGroupReason: String) extends Actor with ActorLogging{
  override def receive: Receive = {
    case GetMessagesForGroup(g) =>
      sender ! GotMessages(g, List())

    case GetAllMessages =>
      sender ! GotMessages("", List())

    case CreateGroup(groupId) =>
      sender ! CannotCreateGroup(groupId, cannotCreateGroupReason)

    case DeleteGroupById(groupId) =>
      sender ! CannotDeleteGroupById(groupId, cannotDeleteGroupReason)

    case AssignMessageToGroup(group, message) =>
      sender ! AssignMessageToGroupFailed(group, message,cannotAssignMessageToGroupReason)
  }
}
