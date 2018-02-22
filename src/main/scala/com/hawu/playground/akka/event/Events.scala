package com.hawu.playground.akka.event


trait PlaygroundStateChangeEvent
case class CreatedMessage(groupId: String, message: String) extends PlaygroundStateChangeEvent
case class CreatedGroup(groupId: String) extends PlaygroundStateChangeEvent
case class DeletedGroup(groupId: String) extends PlaygroundStateChangeEvent