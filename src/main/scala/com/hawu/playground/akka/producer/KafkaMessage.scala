package com.hawu.playground.akka.producer

case class KafkaMessage(timeStamp: Long = 0L, replyToken: String = "", serializedMessage: String = "", objectName: String = "") {
  override def toString: String = {
    f"$timeStamp--$replyToken%s--$serializedMessage%s--$objectName%s"
  }
}

object KafkaMessageBuilder {
  def apply(serialized: String): Option[KafkaMessage] = {
    try {
      val pattern = "([\\S\\s]*)--([\\S\\s]*)--([\\S\\s]*)--([\\S\\s]*)".r
      val pattern(timestamp, hash, message, objectName) = serialized
      Some(KafkaMessage(timestamp.toLong, hash, message, objectName))
    } catch {
        case e: Exception =>
        None
    }
  }
}
