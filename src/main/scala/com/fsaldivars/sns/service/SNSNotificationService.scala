package com.fsaldivars.sns.service

import java.net.URL

import com.amazonaws.services.sns.model._
import com.amazonaws.services.sns.{AmazonSNS, model}

import scala.collection.JavaConverters._
import scala.util.Either
import scala.util.Left
import scala.util.Right


/**
 * Gets singleton instance this will be callled by function lamda
 */
object SNSNotificationService{
  def apply: SNSNotificationService = new SNSNotificationService
}


/**
  * This class provides a wait to centralice all possibles option when  we invoke SNS Service from aws
  * Fidel Saldivar Sanchez
  */
class SNSNotificationService {
  implicit val snsClient: AmazonSNS = SNSConnecctionUtil.snsConnection()

  def confirmSubscription(token: String, arn: String): Option[String] = {
    Option(snsClient.confirmSubscription(new ConfirmSubscriptionRequest().withTopicArn(arn).withToken(token)).getSubscriptionArn)
  }

  def deleteTopic(topic: Topic): Unit = topic.delete()

  /** Creates a topic.*/
  def createTopic(name: String): Option[Topic] =
    try {
      Some(snsClient.createTopic(new CreateTopicRequest(name)).getTopicArn.asTopic)
    } catch {
      case e: Exception =>
        println(e.getMessage)
        None
    }

  /** Creates a topic if it does not exist. */
  def findOrCreateTopic(name: String): Option[Topic] = findTopic(name) orElse { createTopic(name) }

  def findTopic(name: String): Option[Topic] =
    try {
      snsClient.listTopics(new ListTopicsRequest).getTopics.asScala.find(_.name == name)
    } catch {
      case e: Exception =>
        println(e.getMessage)
        None
    }

  def publish(topic: Topic, message: String): Either[PublishResult, ExceptTrace] = topic.publish(message)

  def subscribe(topic: Topic, url: URL): Either[SubscribeResult, ExceptTrace] = topic.subscribe(url)
  

}

trait SNSimplicits {

  implicit class RichString(string: String) {
    def asArn: Arn = Arn(string)

    def asTopic: Topic = new Topic().withTopicArn(string)

    def asUrl: URL = new URL(string)
  }

  implicit class RichTopic(topic: Topic) {
    def arn: Arn = topic.getTopicArn.asArn

    def delete()(implicit snsClient: AmazonSNS): Unit =
      try {
        snsClient.deleteTopic(topic.getTopicArn)
      } catch {
        case e: Exception =>
          println(s"Error on deleteing topic ${e.getMessage}")
      }

    def name: String = topic.arn.name

    def publish(message: String)(implicit snsClient: AmazonSNS): Either[PublishResult, ExceptTrace] = {
      Some(snsClient.publish(new PublishRequest().withTopicArn(topic.getTopicArn).withMessage(message))) match {
        case Some(value) => Left(value)
        case _ => Right(ExceptTrace("Could not publish"))
      }
    }

    def subscribe(url: URL)(implicit snsClient: AmazonSNS): Either[SubscribeResult, ExceptTrace] = {
      Some(snsClient.subscribe(new SubscribeRequest(topic.getTopicArn, url.getProtocol, url.toString))) match {
        case Some(value) => Left(value)
        case _ => Right(ExceptTrace("Could not subscribe"))
      }
    }
  }
}
