package com.hawu.playground.akka.command

import akka.actor.{Actor, ActorLogging}


class CommandController extends Actor with ActorLogging {
  def receive = {
    case cmd: GetMessagesForGroup =>
      sender ! GotMessages(cmd.groupdId, List("aa", "bb"))

    case GetAllMessages =>
      sender ! GotMessages("", List("aa", "bb"))

    case cmd: PlaygroundCommand =>
      log.debug("Got {}", cmd)
  }
}

