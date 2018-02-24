package com.hawu.playground.akka.tests

import com.hawu.playground.akka.command._
import org.scalatest.FlatSpec

class CommandsSerializationTests extends FlatSpec{
  it should "serialize and deserialize messages" in {
    CommandsRegisty (
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

      assert(CommandsRegisty(GetAllMessages().getClass.getTypeName).isDefined)
      assert(CommandsRegisty(GetMessagesForGroup().getClass.getTypeName).isDefined)
      assert(CommandsRegisty(DeleteGroupById().getClass.getTypeName).isDefined)
      assert(CommandsRegisty(GotMessages().getClass.getTypeName).isDefined)
      assert(CommandsRegisty(AssignMessageToGroup().getClass.getTypeName).isDefined)
      assert(CommandsRegisty(AssignMessageToGroupCompleted().getClass.getTypeName).isDefined)
      assert(CommandsRegisty(AssignMessageToGroupFailed().getClass.getTypeName).isDefined)
      assert(CommandsRegisty(GroupByIdDeleted().getClass.getTypeName).isDefined)
      assert(CommandsRegisty(CannotDeleteGroupById().getClass.getTypeName).isDefined)
      assert(CommandsRegisty(CreateGroup().getClass.getTypeName).isDefined)
      assert(CommandsRegisty(GroupCreated().getClass.getTypeName).isDefined)
      assert(CommandsRegisty(CannotCreateGroup().getClass.getTypeName).isDefined)


    assert(GetAllMessages().fromJson(GetAllMessages().asJson) == GetAllMessages())
    assert(GetMessagesForGroup("group").fromJson(GetMessagesForGroup("group").asJson) == GetMessagesForGroup("group"))
    assert(DeleteGroupById("group").fromJson(DeleteGroupById("group").asJson) == DeleteGroupById("group"))
    assert(GotMessages("aa", List("aa")).fromJson(GotMessages("aa", List("aa")).asJson) == GotMessages("aa", List("aa")))
    assert(AssignMessageToGroup("group", "message").fromJson(AssignMessageToGroup("group", "message").asJson) == AssignMessageToGroup("group", "message"))
    assert(AssignMessageToGroupCompleted("group", "message").fromJson(AssignMessageToGroupCompleted("group", "message").asJson) == AssignMessageToGroupCompleted("group", "message"))
    assert(AssignMessageToGroupFailed("group", "message", "reason").fromJson(AssignMessageToGroupFailed("group", "message", "reason").asJson) == AssignMessageToGroupFailed("group", "message", "reason"))
    assert(GroupByIdDeleted("group").fromJson(GroupByIdDeleted("group").asJson) == GroupByIdDeleted("group"))
    assert(CannotDeleteGroupById("group").fromJson(CannotDeleteGroupById("group").asJson) == CannotDeleteGroupById("group"))
    assert(CreateGroup("group").fromJson(CreateGroup("group").asJson) == CreateGroup("group"))
    assert(GroupCreated("group").fromJson(GroupCreated("group").asJson) == GroupCreated("group"))
    assert(CannotCreateGroup("group").fromJson(CannotCreateGroup("group").asJson) == CannotCreateGroup("group"))
  }
}
