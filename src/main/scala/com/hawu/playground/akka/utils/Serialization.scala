package com.hawu.playground.akka.utils

import com.hawu.playground.akka.command.{KafkaJSONDeserializable, KafkaJSONSerializable}

object Serialization {
  def apply(item: KafkaJSONSerializable): Option[String] = {
    try {
      Some(item.asJson)
    } catch {
      case e: Exception => None
    }
  }

  def apply(toDeserialize: KafkaJSONDeserializable, item: String): Option[KafkaJSONDeserializable] = {
    try{
      Some(toDeserialize.fromJson(item))
    } catch  {
      case e: Exception => None
    }
  }
}

