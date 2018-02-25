package com.hawu.playground.akka.event


sealed trait PlaygroundStateChangeEvent
case class CreatedMessage(groupId: String, message: String) extends PlaygroundStateChangeEvent
case class CreatedGroup(groupId: String) extends PlaygroundStateChangeEvent
case class DeletedGroup(groupId: String) extends PlaygroundStateChangeEvent

/*
 * Messages for persistent event sourcing
 * */
sealed trait EventSourcingPersistedEvent
case class PersistentGroupAdded(groupId: String) extends EventSourcingPersistedEvent
case class PersitentGroupRemoved(groupId: String) extends EventSourcingPersistedEvent
case class PersistentMessageCreated(groupdId: String) extends EventSourcingPersistedEvent
case class PersistentMessageRemoved(groupId: String) extends EventSourcingPersistedEvent
