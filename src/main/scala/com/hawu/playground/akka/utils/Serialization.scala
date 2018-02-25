package com.hawu.playground.akka.utils

import scala.util.Try
import com.hawu.playground.akka.command.{KafkaJSONDeserializable, KafkaJSONSerializable}

object Serialization {
  def apply(item: KafkaJSONSerializable): Option[String] = {
    Try(item.asJson).toOption
  }

  def apply(toDeserialize: KafkaJSONDeserializable, item: String): Option[KafkaJSONDeserializable] = {
    Try(toDeserialize.fromJson(item)).toOption
  }
}

