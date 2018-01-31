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
    val subscriptionId = Option(snsClient.confirmSubscription(new ConfirmSubscriptionRequest().withTopicArn(arn).withToken(token)).getSubscriptionArn)
    //Logger.trace(s"SNS Confirmation $subscriptionId")
    subscriptionId
  }

  def deleteTopic(topic: Topic): Unit = topic.delete()

  /** Creates a topic.
    * Topics should contain a string unique to the AWS account, such as the publishing server's domain name
    * @return desired topic */
  def createTopic(name: String): Option[Topic] =
    try {
      val topic = Some(snsClient.createTopic(new CreateTopicRequest(name)).getTopicArn.asTopic)
      //Logger.debug(s"Created SNS topic $topic")
      topic
    } catch {
      case e: Exception =>
        //Logger.warn(e.getMessage)
        None
    }

  /** Creates a topic if it does not exist.
    * Topics should contain a string unique to the AWS account, such as the publishing server's domain name
    * @return newly created topic */
  def findOrCreateTopic(name: String): Option[Topic] = findTopic(name) orElse { createTopic(name) }

  /** @return the topic with the given name if it exists. */
  def findTopic(name: String): Option[Topic] =
    try {
      val topic = snsClient.listTopics(new ListTopicsRequest).getTopics.asScala.find(_.name == name)
      //Logger.debug(s"Found SNS topic $topic")
      topic
    } catch {
      case e: Exception =>
        //Logger.warn(e.getMessage)
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
       // Logger.debug(s"SNS topic ${topic.getTopicArn} was deleted")
      } catch {
        case e: Exception =>
          println(s"Error on deleteing topic ${e.getMessage}")
       //   Logger.warn(e.getMessage)
      }

    def name: String = topic.arn.name

    /** @return published message id */
    def publish(message: String)(implicit snsClient: AmazonSNS): Either[PublishResult, ExceptTrace] = {
      Some(snsClient.publish(new PublishRequest().withTopicArn(topic.getTopicArn).withMessage(message))) match {
        case Some(value) => Left(value)
        case _ => Right(ExceptTrace("Could not publish"))
      }
      //Logger.debug(s"Published SNS message $message")
    }

    /** @param url will receive HTTP POST of ConfirmSubscription action
      * @return ARN of subscription */
    def subscribe(url: URL)(implicit snsClient: AmazonSNS): Either[SubscribeResult, ExceptTrace] = {
      Some(snsClient.subscribe(new SubscribeRequest(topic.getTopicArn, url.getProtocol, url.toString))) match {
        case Some(value) => Left(value)
        case _ => Right(ExceptTrace("Could not subscribe"))
      }
      //Logger.debug(s"Subscribed to SNS endpoint $url")
    }
  }


}
