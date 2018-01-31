package com.fsaldivars.sns.service

import java.io.FileInputStream
import java.util.Properties

import com.amazonaws.auth.{AWSCredentialsProvider, AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.regions.Regions
import com.amazonaws.services.sns.AmazonSNSClientBuilder

object SNSConnecctionUtil {

  val(region, accessKey, secretKey) = {
    try{
      val prop = new Properties()
      prop.load(new FileInputStream("config.properties"))
      ( prop.getProperty("aws.region") ,
        prop.getProperty("aws.accessKeyId"),
        prop.getProperty("aws.secretAccessKey")
      )

    }catch { case e: Exception =>
      println(s"Exception ocurred loading files from config propeties ${e.getMessage}")
      ("","","")
    }
  }

  def snsConnection() ={

    AmazonSNSClientBuilder
      .standard()
      .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
      .withRegion(region)
      .build()
  }

}

/**
  * To avoid implicit value
  */
trait RegionsImplict{

  implicit class RichRegion(region: String){
    def asRegion: Regions = Regions.values().find(_.name == region).getOrElse(Regions.EU_WEST_1)
  }

}
