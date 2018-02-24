package com.hawu.playground.akka.event

import akka.actor.{Actor, ActorLogging}
import com.hawu.playground.akka.command.{AssignMessageToGroup, CreateGroup, GetAllMessages, GetMessagesForGroup}

import scala.collection.immutable.HashMap


class PlaygroundPersistentState() extends Actor with ActorLogging {

  var registry: HashMap[String, List[String]] = HashMap()

  def receive = {
   case msg: GetAllMessages =>

   case msg: GetMessagesForGroup =>
   case msg: CreateGroup =>
    msg.command()
   case msg: DeletedGroup =>
   case msg: AssignMessageToGroup =>
  }
}
