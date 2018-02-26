package com.hawu.playground.akka.tests

import com.hawu.playground.akka.command._
import com.hawu.playground.akka.utils.Serialization
import org.scalatest.FlatSpec

class CommandsSerializationTests extends FlatSpec {

  CommandsRegisty(
    Seq(
      GetAllMessages(),
      GetMessagesForGroup(),
      DeleteGroupById(),
      GotMessages(),
      AssignMessageToGroup(),
      AssignMessageToGroupCompleted(),
      AssignMessageToGroupFailed(),
      GroupByIdDeleted(),
      CannotDeleteGroupById(),
      CreateGroup(),
      GroupCreated(),
      CannotCreateGroup()
    )
  )


  "Messages " should " be in registry" in {

    val messages = Seq(
      GetAllMessages(),
      GetMessagesForGroup(),
      DeleteGroupById(),
      GotMessages(),
      AssignMessageToGroup(),
      AssignMessageToGroupCompleted(),
      AssignMessageToGroupFailed(),
      GroupByIdDeleted(),
      CannotDeleteGroupById(),
      CreateGroup(),
      GroupCreated(),
      CannotCreateGroup()
    )

    messages.map(message => {
      assert(CommandsRegisty.isInRegistry_?(message))
    })
  }

  "Messages " should "serialize and deserialize properly" in {
    val messages = Seq(
      GetAllMessages(),
      GetMessagesForGroup("group"),
      DeleteGroupById("group"),
      GotMessages("aa", List("aa")),
      AssignMessageToGroup("group", "message"),
      AssignMessageToGroupCompleted("group", "message"),
      AssignMessageToGroupFailed("group", "message", "reason"),
      GroupByIdDeleted("group"),
      CannotDeleteGroupById("group"),
      CreateGroup("group"),
      GroupCreated("group"),
      CannotCreateGroup("group")
    )

    messages.map(originalMessage => {
      assert(Serialization(originalMessage).isDefined)
      Serialization(originalMessage) map {
        serializated => {
          assert(Serialization(originalMessage, serializated).isDefined)
          Serialization(originalMessage, serializated) map {
            deserializated => {
              assert(deserializated == originalMessage)
            }
          }
        }
      }
    })
  }

}
