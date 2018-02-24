package com.hawu.playground.akka.utils

import com.hawu.playground.akka.parsers.{StringParser, StringParserNotParseAbleException}

import scala.xml.{Node, NodeSeq}


trait XMLSerializable

object NodeSeqValueGetter {
  def apply[T](item: NodeSeq, default: T): Option[T] = {
    try {
      Some(item.headOption.map(item => item.asInstanceOf[T]).getOrElse(default))
    } catch {
      case t: Exception =>
        None
    }

  }
}

object Serialization {
  def apply(item: XMLSerializable): String = {
    <obj name={item.getClass.getName}>
        {item.getClass.getDeclaredFields.map(f => {
      val t = f.getType.getName
      if(StringParser.isStringParseable_?(t)) {
        f.setAccessible(true)
        val value = f.get(item)
          <field name={f.getName} value={value.toString} type={t}/>
      } else {
        val t = f.getType.getName
        throw new StringParserNotParseAbleException(f"Cannot parse type $t%s, see object StringParser")
      }
    })}
    </obj>.toString()
  }

  def apply(item: String): Option[Any] = {
    val xml = scala.xml.XML.loadString(item)

    var toReturn: Option[Any] = None

    try {
      xml.foreach(node => {
        val className = (node \ "@name").toString
        val cl = Class.forName(className)

        var argsNames: List[String] = List()
        val args = (node \\ "field").map(field => {
          val t = (field \ "@type").toString
          val v = (field \ "@value").toString
          val n = (field \ "@name").toString
          argsNames = t :: argsNames
          StringParser(t, v)
        }).toList

        val argsQuery = argsNames.foldLeft("")(_ + "," + _)
        val searString = {if(!argsQuery.isEmpty) {
          argsQuery.substring(1)
        } else {
          ""
        }}

        val method = cl
        .getMethods
        .find(x => x.getName == "apply" && x.toString.contains(searString))
        .get

        toReturn = Some(
          method.invoke(cl, args map (_.asInstanceOf[AnyRef]): _*)
        )
      })

      toReturn
    } catch {
      case e: Exception =>
        e.fillInStackTrace()
        None
    }
  }
}

