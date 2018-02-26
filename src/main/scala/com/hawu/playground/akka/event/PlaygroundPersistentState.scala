package com.hawu.playground.akka.event

import akka.actor.{Actor, ActorLogging}
import com.hawu.playground.akka.command._

import scala.collection.immutable.HashMap


class PlaygroundPersistentState() extends Actor with ActorLogging {

  // FIXME really dislike this mutation, use actor.become
  var registry: HashMap[String, List[String]] = HashMap()

  def receive = {

    case msg: GetAllMessages   =>
      sender !  GotMessages("", registry.flatMap(m => m._2).toList)

    case msg: GetMessagesForGroup =>
      sender ! GotMessages(
        msg.groupdId,
        registry
          .find(_._1 == msg.groupdId)
          .map(_._2)
          .getOrElse(Nil)
      )

    case msg: CreateGroup if (registry.exists(_._1 == msg.groupId)) =>
      sender ! CannotCreateGroup(msg.groupId, "Already exists")

    case msg: CreateGroup =>
      msg.command.foreach(self ! _)
      sender ! GroupCreated(msg.groupId)

    case msg: DeleteGroupById if (registry.exists(_._1 == msg.groupId)) =>
      msg.command.foreach(self ! _)
      sender ! GroupByIdDeleted(msg.groupId)

    case msg: DeleteGroupById =>
      sender ! CannotDeleteGroupById(msg.groupId, "Group doens't exists")

    case msg: AssignMessageToGroup if (registry.exists(_._1 == msg.groupId)) =>
      msg.command.foreach(self ! _)
      sender ! AssignMessageToGroupCompleted(msg.groupId, msg.message)

    case msg: AssignMessageToGroup =>
      sender ! AssignMessageToGroupFailed(msg.groupId, msg.message, "Group doens't exists")

    case msg: CreatedGroup =>
      registry = registry + (msg.groupId -> Nil)

    case msg: DeletedGroup =>
      registry = registry.filterNot(_._1 == msg.groupId)

    case msg: CreatedMessage =>
      registry =
        registry.filterNot(_._1 == msg.groupId) + (
        registry
        .find(_._1 == msg)
          .map(found => found._1 -> (msg.message :: found._2.filterNot(_ == msg.message)))
          .getOrElse(msg.groupId -> List(msg.message))
        )

    case other =>
      log.error("Got unknown event!! {}", other)
  }
}
