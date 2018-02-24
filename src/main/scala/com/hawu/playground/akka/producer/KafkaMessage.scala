package com.hawu.playground.akka.producer

case class KafkaMessage(timeStamp: Long = 0L, replyToken: String = "", serializedMessage: String = "") {
  override def toString: String = {
    f"$timeStamp-$replyToken%s-$serializedMessage%s"
  }
}

object KafkaMessageBuilder {
  def apply(serialized: String): Option[KafkaMessage] = {
    try {
      val pattern = "(.*)-(.*)-(.*)".r
      val pattern(timestamp, hash, messge) = serialized
      Some(KafkaMessage(timestamp.toLong, hash, messge))
    } catch {
        case e: Exception =>
        None
    }
  }
}
