package com.hawu.playground.akka.command

//todo: It would be great to rollback events after timeout

import com.hawu.playground.akka.event.{CreatedGroup, CreatedMessage, DeletedGroup, PlaygroundStateChangeEvent}
import com.hawu.playground.akka.utils.{NodeSeqValueGetter,  XMLSerializable}

trait PlaygroundCommand extends XMLSerializable {
  def command(): List[PlaygroundStateChangeEvent]
}

trait CommandRequiresResponse

trait HttpCommandResponse extends XMLSerializable {
}

case object CommandTimeouted
//Get messages command

case class GetMessagesForGroup(groupdId: String) extends PlaygroundCommand with CommandRequiresResponse {
  override def command(): List[PlaygroundStateChangeEvent] = List()
}

case class GetAllMessages() extends PlaygroundCommand with CommandRequiresResponse  {
  override def command(): List[PlaygroundStateChangeEvent] = List()
}

case class GotMessages(groupId: String, messages: List[String]) extends HttpCommandResponse

//Assign message to group command
case class AssignMessageToGroup(groupId: String, message: String) extends PlaygroundCommand with CommandRequiresResponse {
  override def command(): List[PlaygroundStateChangeEvent] = CreatedMessage(groupId, message) :: Nil
}

case class AssignMessageToGroupCompleted(groupId: String, message: String) extends HttpCommandResponse

case class AssignMessageToGroupFailed(groupId: String, message: String, reason: String) extends HttpCommandResponse

//Delete group by id command
case class DeleteGroupById(groupId: String) extends PlaygroundCommand with CommandRequiresResponse {
  override def command(): List[PlaygroundStateChangeEvent] = DeletedGroup(groupId) :: Nil
}

case class GroupByIdDeleted(groupId: String) extends HttpCommandResponse

case class CannotDeleteGroupById(groupId: String, reason: String) extends HttpCommandResponse

//Create group by id command
case class CreateGroup(groupId: String) extends PlaygroundCommand with CommandRequiresResponse {
  override def command(): List[PlaygroundStateChangeEvent] = CreatedGroup(groupId) :: Nil
}

case class GroupCreated(groupId: String) extends HttpCommandResponse

case class CannotCreateGroup(groupId: String, reason: String) extends HttpCommandResponse


