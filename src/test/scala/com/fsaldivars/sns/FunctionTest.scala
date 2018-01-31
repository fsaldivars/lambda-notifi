package com.fsaldivars.sns

import com.teamknowlogy.sns.service._
import org.scalatest.concurrent.AsyncAssertions
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, MustMatchers, Suite}
import org.scalatest.FlatSpec

class FunctionTest extends FlatSpec with SpecServiceTest
{
  
  "A SNS " should "Create a  toic in AWS::SNS" in {
    snsCreateTopicFunction.handleRequest(TopicRequest(uuid), null)
  }
  
  "A SNS With Topic" should "Subscribe by url" in {
    snsService.createTopic(uuid) match {
      case Some(value) => snsSubscribeFunction.handleRequest(SubscriptionRequest(value.getTopicArn, "https://console.aws.amazon.com/sns/v2/home?region=us-east-1#/topics"), null)
      case _ => println(""); //should be thrownBy {ExceptTrace("Error in test")}
    }
	  
  }
  
  "A SNS With Topic and subscription" should "publish message into topic" in {
     snsService.createTopic(uuid) match {
      case Some(value) => snsPublishFunction.handleRequest(PublishMessageRequest(value.getTopicArn, "Hola this is full test in message body"), null)
      case _ => println(""); //should be thrownBy {ExceptTrace("Error in test")}
    }
    //
  }
      
  
}