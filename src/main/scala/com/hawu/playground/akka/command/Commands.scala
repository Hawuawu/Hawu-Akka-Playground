package com.hawu.playground.akka.command

import com.hawu.playground.akka.event.{CreatedGroup, CreatedMessage, DeletedGroup, PlaygroundStateChangeEvent}
import spray.json._


trait CommandsRegistyRegisterable

trait KafkaJSONSerializable {
  def asJson: String
}

trait KafkaJSONDeserializable {
  def fromJson(json: String): KafkaJSONDeserializable
}

trait PlaygroundCommand extends KafkaJSONSerializable with KafkaJSONDeserializable with CommandsRegistyRegisterable {
  def command(): List[PlaygroundStateChangeEvent]
}

trait CommandRequiresResponse

trait HttpCommandResponse extends KafkaJSONSerializable with KafkaJSONDeserializable with CommandsRegistyRegisterable {}

case object CommandTimeouted
//Get messages command

object GetMessagesForGroupProtocol extends DefaultJsonProtocol {
  implicit val getMessagesForGroup = jsonFormat1(GetMessagesForGroup)
}

case class GetMessagesForGroup(groupdId: String = "") extends PlaygroundCommand with CommandRequiresResponse {

  import GetMessagesForGroupProtocol._

  override def command(): List[PlaygroundStateChangeEvent] = List()

  override def asJson: String = {
    this.toJson.compactPrint
  }

  override def fromJson(json: String): KafkaJSONDeserializable =
  {
    val js = json.parseJson
    js.convertTo[GetMessagesForGroup]
  }

}

object GetAllMessagesProtocol extends DefaultJsonProtocol {
  implicit val getAllMessages = jsonFormat0(GetAllMessages)
}

case class GetAllMessages() extends PlaygroundCommand with CommandRequiresResponse  {
  override def command(): List[PlaygroundStateChangeEvent] = List()

  import GetAllMessagesProtocol._

  override def asJson: String = {
    this.toJson.compactPrint
  }

  override def fromJson(json: String): KafkaJSONDeserializable =
  {
    val js = json.parseJson
    js.convertTo[GetAllMessages]
  }
}

object GotMessagesProtocol extends DefaultJsonProtocol {
  implicit val gotMessages = jsonFormat2(GotMessages)
}

case class GotMessages(groupId: String = "", messages: List[String] = List()) extends HttpCommandResponse
{

  import GotMessagesProtocol._

  override def asJson: String = {
    this.toJson.compactPrint
  }

  override def fromJson(json: String): KafkaJSONDeserializable = {
    val js = json.parseJson
    js.convertTo[GotMessages]
  }
}

object AssignMessageToGroupProtocol extends DefaultJsonProtocol {
  implicit val assignMessageToGroup = jsonFormat2(AssignMessageToGroup)
}

//Assign message to group command
case class AssignMessageToGroup(groupId: String = "", message: String = "") extends PlaygroundCommand with CommandRequiresResponse {
  override def command(): List[PlaygroundStateChangeEvent] = CreatedMessage(groupId, message) :: Nil

  import AssignMessageToGroupProtocol._

  override def asJson: String = {
    this.toJson.compactPrint
  }

  override def fromJson(json: String): KafkaJSONDeserializable = {
    val js = json.parseJson
    js.convertTo[AssignMessageToGroup]
  }
}

object AssignMessageToGroupCompletedProtocol extends DefaultJsonProtocol {
  implicit val assignMessageToGroupCompleted = jsonFormat2(AssignMessageToGroupCompleted)
}

case class AssignMessageToGroupCompleted(groupId: String = "", message: String = "") extends HttpCommandResponse
{

  import AssignMessageToGroupCompletedProtocol._

  override def asJson: String = {
    this.toJson.compactPrint
  }

  override def fromJson(json: String): KafkaJSONDeserializable = {
    json.parseJson.convertTo[AssignMessageToGroupCompleted]
  }
}

object AssignMessageToGroupFailedProtocol extends DefaultJsonProtocol {
  implicit val assignMessageToGroupFailed = jsonFormat3(AssignMessageToGroupFailed)
}


case class AssignMessageToGroupFailed(groupId: String = "", message: String = "", reason: String = "") extends HttpCommandResponse {
  import AssignMessageToGroupFailedProtocol._

  override def asJson: String = {
    this.toJson.compactPrint
  }

  override def fromJson(json: String): KafkaJSONDeserializable = {
    json.parseJson.convertTo[AssignMessageToGroupFailed]
  }
}


object DeleteGroupByIdProtocol extends DefaultJsonProtocol {
  implicit val deleteGroupById = jsonFormat1(DeleteGroupById)
}

//Delete group by id command
case class DeleteGroupById(groupId: String = "") extends PlaygroundCommand with CommandRequiresResponse {
  import DeleteGroupByIdProtocol._

  override def command(): List[PlaygroundStateChangeEvent] = DeletedGroup(groupId) :: Nil

  override def asJson: String = {
    this.toJson.compactPrint
  }

  override def fromJson(json: String): KafkaJSONDeserializable = {
    json.parseJson.convertTo[DeleteGroupById]
  }
}

object GroupByIdDeletedProtocol extends DefaultJsonProtocol {
  implicit val groupByIdDeleted = jsonFormat1(GroupByIdDeleted)
}
case class GroupByIdDeleted(groupId: String = "") extends HttpCommandResponse {

  import GroupByIdDeletedProtocol._

  override def asJson: String = {
    this.toJson.compactPrint
  }

  override def fromJson(json: String): KafkaJSONDeserializable = {
    json.parseJson.convertTo[GroupByIdDeleted]
  }
}


object CannotDeleteGroupByIdProtocol extends DefaultJsonProtocol {
  implicit val cannotDeleteGroupById = jsonFormat2(CannotDeleteGroupById)
}
case class CannotDeleteGroupById(groupId: String = "", reason: String = "") extends HttpCommandResponse {

  import CannotDeleteGroupByIdProtocol._

  override def asJson: String = {
    this.toJson.compactPrint
  }

  override def fromJson(json: String): KafkaJSONDeserializable = {
    json.parseJson.convertTo[CannotDeleteGroupById]
  }
}

object CreateGroupProtocol extends DefaultJsonProtocol {
  implicit val createGroup = jsonFormat1(CreateGroup)
}
case class CreateGroup(groupId: String = "") extends PlaygroundCommand with CommandRequiresResponse {
  override def command(): List[PlaygroundStateChangeEvent] = CreatedGroup(groupId) :: Nil

  import CreateGroupProtocol._

  override def asJson: String = {
    this.toJson.compactPrint
  }

  override def fromJson(json: String): KafkaJSONDeserializable = {
    json.parseJson.convertTo[CreateGroup]
  }
}

object GroupCreatedProtocol extends DefaultJsonProtocol {
  implicit val groupCreated = jsonFormat1(GroupCreated)
}
case class GroupCreated(groupId: String = "") extends HttpCommandResponse {


  import GroupCreatedProtocol._

  override def asJson: String = {
    this.toJson.compactPrint
  }

  override def fromJson(json: String): KafkaJSONDeserializable = {
    json.parseJson.convertTo[GroupCreated]
  }
}

object CannotCreateGroupProtocol extends DefaultJsonProtocol {
  implicit val cannotCreateGroup = jsonFormat2(CannotCreateGroup)
}
case class CannotCreateGroup(groupId: String = "", reason: String = "") extends HttpCommandResponse {


  import CannotCreateGroupProtocol._

  override def asJson: String = {
    this.toJson.compactPrint
  }

  override def fromJson(json: String): KafkaJSONDeserializable = {
    json.parseJson.convertTo[CannotCreateGroup]
  }
}


object CommandsRegisty {

  var registry: Seq[CommandsRegistyRegisterable] = Seq()

  def apply(s: Seq[CommandsRegistyRegisterable]) = {
    registry = s ++ registry
  }

  def apply(s: String): Option[CommandsRegistyRegisterable] = {
    registry.find(_.getClass.getTypeName == s)
  }

}
