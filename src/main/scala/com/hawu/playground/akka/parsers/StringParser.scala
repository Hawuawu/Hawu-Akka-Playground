package com.hawu.playground.akka.parsers

case class StringParserNotParseAbleException(messaage:String) extends Exception

object StringParser {
  val byteJavaName = classOf[java.lang.Byte].getName
  val shortJavaName = classOf[java.lang.Short].getName
  val integerJavaName = classOf[java.lang.Integer].getName
  val longJavaName = classOf[java.lang.Long].getName
  val floatJavaName = classOf[java.lang.Float].getName
  val doubleJavaName = classOf[java.lang.Double].getName
  val characterJavaName = classOf[java.lang.Character].getName
  val stringJavaName = classOf[java.lang.String].getName
  val booleanJavaName = classOf[java.lang.Boolean].getName

  val byteScalaName = classOf[scala.Byte].getName
  val shortScalaName = classOf[scala.Short].getName
  val integerScalaName = classOf[scala.Int].getName
  val longScalaName = classOf[scala.Long].getName
  val floatScalaName = classOf[scala.Float].getName
  val doubleScalaName = classOf[scala.Double].getName
  val characterScalaName = classOf[scala.Char].getName
  val booleanScalaName = classOf[scala.Boolean].getName

  def isStringParseable_?(t: String): Boolean = getClass.getDeclaredFields.exists(f => {
    f.setAccessible(true)
    val splitted = t.split("\\.")
    val searchingFor = splitted.last.toLowerCase
    if(splitted.nonEmpty) {
      f.getName.toLowerCase.contains(splitted.last.toLowerCase)
    } else {
      f.getName.toLowerCase.contains(t)
    }
  })

  def apply(t: String, value: String): Any = {
    t match {
      case `byteJavaName` =>
        java.lang.Byte.parseByte(value)

      case `shortJavaName` =>
        java.lang.Short.parseShort(value)

      case `integerJavaName` =>
        java.lang.Integer.parseInt(value)

      case `longJavaName` =>
        java.lang.Long.parseLong(value)

      case `floatJavaName` =>
        java.lang.Float.parseFloat(value)

      case `doubleJavaName` =>
        java.lang.Double.parseDouble(value)

      case `characterJavaName` =>
        value.charAt(0)

      case `stringJavaName` =>
        value

      case `booleanJavaName` =>
        java.lang.Boolean.parseBoolean(value)

      case `byteScalaName` =>
        value.toByte

      case `shortScalaName` =>
        value.toShort

      case `integerScalaName` =>
        value.toInt

      case `longScalaName` =>
        value.toLong

      case `floatScalaName` =>
        value.toFloat

      case `doubleScalaName` =>
        value.toDouble

      case `characterScalaName` =>
        value.charAt(0)

      case `booleanScalaName` =>
        value.toBoolean
    }
  }
}
