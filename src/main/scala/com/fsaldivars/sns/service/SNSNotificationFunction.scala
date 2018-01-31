package com.fsaldivars.sns.service

import scala.beans.BeanProperty
import com.amazonaws.services.lambda.runtime.{Context, RequestHandler}
import akka.http.scaladsl.model.StatusCode
import org.apache.http.HttpStatus
import com.amazonaws.services.sns.model.Topic
import scala.util.Right
import scala.util.Left





case class TopicRequest(@BeanProperty name: String){def this() = this(name = "")}
case class SubscriptionRequest(@BeanProperty topicArn: String, @BeanProperty url: String){def this() = this(topicArn = "", url = "")}
case class PublishMessageRequest(@BeanProperty topicArn: String, @BeanProperty message: String){def this() = this(topicArn = "", message = "")}

/**
 * This class is colled by lambda function in order to get success access by SNS Client and do almost all functionality in SNS
 */
class SNSNotificationFunction extends RequestHandler[TopicRequest, ProxyResponse]{
  
  implicit val snsService: SNSNotificationService = SNSNotificationService.apply
  
  override def handleRequest(input: TopicRequest, ctx: Context): ProxyResponse ={
    snsService.findOrCreateTopic(input.name) match {
      case Some(value) => ProxyResponse.apply(HttpStatus.SC_OK, jsonPrettyPrint(value))
      case _ => ProxyResponse.apply(HttpStatus.SC_BAD_REQUEST, "error.code")
    }
  }
}

/**
 * This class is colled by lambda function in order to get success access by SNS Client and do almost all functionality in SNS
 */
class SNSNotificationSubscribeFunction extends RequestHandler[SubscriptionRequest, ProxyResponse]{
  
  implicit val snsService: SNSNotificationService = SNSNotificationService.apply
  
  override def handleRequest(input: SubscriptionRequest, ctx: Context): ProxyResponse ={
    snsService.findTopic(input.topicArn) match {
      case Some(value) => subscribe(value, input.url)
      case _ => ProxyResponse.apply(HttpStatus.SC_BAD_REQUEST, "error.code")
    }
  }
  def subscribe(topic: Topic, url: String): ProxyResponse ={
    snsService.subscribe(topic, url.asUrl) match {
      case Left(value) => ProxyResponse.apply(HttpStatus.SC_OK, jsonPrettyPrint(value))
      case Right(ex) => ProxyResponse.apply(HttpStatus.SC_BAD_REQUEST, "error.code")
    }
  }
}


/**
 * This class is colled by lambda function in order to get success access by SNS Client and do almost all functionality in SNS
 */
class SNSNotificationPublishFunction extends RequestHandler[PublishMessageRequest, ProxyResponse]{
  
  implicit val snsService: SNSNotificationService = SNSNotificationService.apply
  
  override def handleRequest(input: PublishMessageRequest, ctx: Context): ProxyResponse ={
    snsService.findTopic(input.topicArn) match {
      case Some(value) => publish(value, input.message)
      case _ => ProxyResponse.apply(HttpStatus.SC_BAD_REQUEST, "error.code")
    }
  }
  def publish(topic: Topic, message: String): ProxyResponse ={
    snsService.publish(topic, message) match {
      case Left(value) => ProxyResponse.apply(HttpStatus.SC_OK, jsonPrettyPrint(value))
      case Right(ex) => ProxyResponse.apply(HttpStatus.SC_BAD_REQUEST, "error.code")
    }
  }
}

/**
 * In order to avoid extra initialization on my apply method
 */
trait Utilresponse {
  def headers = Some(Map("Access-Control-Allow-Origin" -> "*"))
}
case class ProxyResponse(
                            statusCode: Int,
                            body: String
                          ) extends Utilresponse
