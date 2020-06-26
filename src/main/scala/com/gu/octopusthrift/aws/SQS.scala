package com.gu.octopusthrift.aws

import com.amazonaws.services.sqs.model.{ SendMessageRequest, SendMessageResult }
import com.amazonaws.services.sqs.{ AmazonSQS, AmazonSQSClientBuilder }
import com.gu.octopusthrift.Config
import com.gu.octopusthrift.services.Logging
import play.api.libs.json._

import scala.util.{ Failure, Success, Try }

class SQS(config: Config) extends Logging {

  private val builder = AmazonSQSClientBuilder.defaultClient()

  private lazy val sqsClient: AmazonSQS = builder

  def sendMessage(message: JsValue) = {

    val request =
      new SendMessageRequest().withQueueUrl(config.deadLetterQueue).withMessageBody(Json.stringify(message))

    Try(sqsClient.sendMessage(request)) match {
      case Success(messageResult: SendMessageResult) =>
        logger.info(s"Message sent to dead letter queue with ID ${messageResult.getMessageId}")
      case Failure(e) => s"Dead letter sendMessage Exception: ${e.getMessage}"
    }
  }

}
