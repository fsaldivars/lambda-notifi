package com.fsaldivars

package object sns extends SNSimplicits{

  import org.slf4j.LoggerFactory
  import com.amazonaws.util.json.Jackson
  import scala.language.implicitConversions

  private[service] lazy val Logger = LoggerFactory.getLogger("aws-sns")

  def jsonPrettyPrint(awsObject: Object): String = Jackson.toJsonPrettyString(awsObject)

  def uuid: String = java.util.UUID.randomUUID.toString

  implicit class RichException(exception: Exception) {
    def prefixMsg(msg: String): Exception = ExceptTrace(msg + "\n" +exception.getMessage)
  }




}
