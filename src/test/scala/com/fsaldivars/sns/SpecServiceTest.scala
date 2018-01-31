package com.fsaldivars.sns

import com.teamknowlogy.sns.service.SNSimplicits
import com.teamknowlogy.sns.service._
import org.scalatest._


/**
 * Test by creating aour implementation of singleton
 */
trait SpecServiceTest extends SNSimplicits with Matchers{
      lazy implicit val snsCreateTopicFunction: SNSNotificationFunction = new SNSNotificationFunction()
		  lazy implicit val snsSubscribeFunction: SNSNotificationSubscribeFunction = new SNSNotificationSubscribeFunction()
		  lazy implicit val snsPublishFunction: SNSNotificationPublishFunction = new SNSNotificationPublishFunction()
		  lazy implicit val snsService: SNSNotificationService = new SNSNotificationService()
}