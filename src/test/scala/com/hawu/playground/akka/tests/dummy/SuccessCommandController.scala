package com.hawu.playground.akka.tests.dummy

import akka.actor.{Actor, ActorLogging}
import com.hawu.playground.akka.command._

class SuccessCommandController(testMessages: List[String]) extends Actor with ActorLogging{
  override def receive: Receive = {
    case GetMessagesForGroup(g) =>
      sender ! GotMessages(g, testMessages)

    case GetAllMessages =>
      sender ! GotMessages("", testMessages)

    case CreateGroup(groupId) =>
      sender ! GroupCreated(groupId)

    case DeleteGroupById(groupId) =>
      sender ! GroupByIdDeleted(groupId)

    case AssignMessageToGroup(group, message) =>
      sender ! AssignMessageToGroupCompleted(group, message)
  }
}
