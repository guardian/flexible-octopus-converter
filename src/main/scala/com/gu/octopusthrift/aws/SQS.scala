package com.gu.octopusthrift.aws

import com.amazonaws.services.sqs.model.{ SendMessageRequest }
import com.amazonaws.services.sqs.{ AmazonSQS, AmazonSQSClientBuilder }
import com.gu.octopusthrift.services.Logging
import com.gu.octopusthrift.Config
import play.api.libs.json._

class SQS(config: Config) extends Logging {

  private val builder = AmazonSQSClientBuilder.defaultClient()

  private lazy val sqsClient: AmazonSQS = builder

  def sendMessage(message: JsValue) = {

    val request =
      new SendMessageRequest().withQueueUrl(config.deadLetterQueue).withMessageBody(Json.stringify(message))

    try {
      logger.info(s"Message sent to dead letter queue")
      sqsClient.sendMessage(request)
    } catch {
      case e: Exception =>
        logger.info(s"Dead letter sendMessage Exception: ${e.getMessage}")
    }

  }

}
