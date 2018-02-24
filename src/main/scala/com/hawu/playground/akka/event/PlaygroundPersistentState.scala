package com.hawu.playground.akka.event

import akka.actor.{Actor, ActorLogging}
import com.hawu.playground.akka.command._

import scala.collection.immutable.HashMap


class PlaygroundPersistentState() extends Actor with ActorLogging {

  var registry: HashMap[String, List[String]] = HashMap(
    "group1" -> List("message1", "message2"),
    "group2" -> List("message1", "message2")
  )

  def receive = {
   case msg: GetAllMessages   =>
    sender ! GotMessages("", registry.flatMap(m => m._2).toList)

   case msg: GetMessagesForGroup =>
     sender ! GotMessages(
       msg.groupdId,
       registry
         .find(f =>f._1 == msg.groupdId).map(m => m._2).getOrElse(List())
     )

   case msg: CreateGroup =>
    if(registry.exists(r => r._1 == msg.groupId))
      {
        sender ! CannotCreateGroup(msg.groupId, "Already exists")
      } else {
      val events = msg.command()
      sender ! GroupCreated(msg.groupId)
    }

   case msg: DeleteGroupById =>
     if(registry.exists(r => r._1 == msg.groupId))
     {
       sender ! CannotDeleteGroupById(msg.groupId, "Group doens't exists")
     } else {
       val events = msg.command()
       sender ! GroupByIdDeleted(msg.groupId)
     }

   case msg: AssignMessageToGroup =>
     if(registry.exists(r => r._1 == msg.groupId))
     {
       sender ! AssignMessageToGroupFailed(msg.groupId, msg.message, "Group doens't exists")
     } else {
       val events = msg.command()
       sender ! AssignMessageToGroupCompleted(msg.groupId, msg.message)
     }

   case msg: PlaygroundStateChangeEvent =>
      msg match {
        case m: CreatedGroup =>
          registry = registry + (m.groupId -> List())

        case m: DeletedGroup =>
          registry = registry.filterNot(f => f._1 == m.groupId)

        case m: CreatedMessage =>
          registry =
            registry
              .filterNot(f => f._1 == m.groupId) +
              registry
              .find(f => f._1 == m)
                .map(found => found._1 -> (m.message :: found._2.filterNot(f => f == m.message)))
                  .getOrElse(m.groupId -> List(m.message))

        case other =>
          log.error("Got unknown event!! {}", other)
      }
  }
}
