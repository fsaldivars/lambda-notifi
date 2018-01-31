package com.fsaldivars.sns.service

import scala.util.control.NoStackTrace
/**
  * Provides a way to extend functionality in order to be more clear in service
  */
case class Arn(arnStr: String) {
  import com.amazonaws.services.sns.model.Topic

  println(s"Current values for response=$arnStr")

  val Array(_, _, serviceName, region, amiOwnerId, name) = arnStr.split(":")

  def asTopic: Topic = new Topic().withTopicArn(arnStr)

}

class ExceptTrace(msg: String) extends Exception(msg) with NoStackTrace

object ExceptTrace {
  def apply(msg: String): ExceptTrace = new ExceptTrace(msg)

  def apply(msg: String, exception: Exception): ExceptTrace = new ExceptTrace(msg)
}

case class Subscription(arnStr: Arn) {
  import com.amazonaws.services.sns.AmazonSNSClient

  def confirm(token: String)(implicit snsClient: AmazonSNSClient): Option[Arn] = {
    import com.amazonaws.services.sns.model.ConfirmSubscriptionRequest

    val request = new ConfirmSubscriptionRequest().withTopicArn(arnStr.name).withToken(token)
    val maybeSubscriptionArn = Option(snsClient.confirmSubscription(request).getSubscriptionArn.asArn)
    Logger.trace(s"SNS Subscription confirmed with Arn ${maybeSubscriptionArn.get}")
    maybeSubscriptionArn
  }


}
