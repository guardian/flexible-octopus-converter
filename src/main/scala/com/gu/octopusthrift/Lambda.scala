package com.gu.octopusthrift

import com.amazonaws.services.kinesis.model.Record
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.KinesisEvent
import com.gu.flexibleoctopus.model.thrift._
import com.gu.octopusthrift.aws.{ CloudWatch, Kinesis, SQS }
import com.gu.octopusthrift.models._
import com.gu.octopusthrift.services.Logging
import com.gu.octopusthrift.services.PayloadValidator.{ isValidBundle, validatePayload }
import com.gu.octopusthrift.util.ThriftSerializer.serializeToBytes
import play.api.libs.json._

import scala.jdk.CollectionConverters._
import scala.util.{ Failure, Success, Try }

object Lambda extends Logging {

  val deadLetterQueue = new SQS(Config.apply)

  def handler(lambdaInput: KinesisEvent, context: Context): Unit = {
    val records: List[Record] = lambdaInput.getRecords.asScala.map(_.getKinesis).toList

    records.foreach(record => {
      val data = record.getData().array()
      val validatedPayload: Option[OctopusPayload] = validatePayload(data)
      val sequenceNumber = record.getSequenceNumber

      validatedPayload.foreach(payload => {
        if (payload.bundles.isDefined) {
          val messageIndex = payload.thismessageindex.getOrElse(0)
          val totalMessages = payload.totalmessages.getOrElse(0)
          logger.info(
            s"Processing daily snapshot, message $messageIndex of $totalMessages, sequence number: $sequenceNumber")
          payload.bundles.get.foreach(bundle => processBundle(bundle, sequenceNumber))
        } else if (payload.data.isDefined) {
          logger.info(s"Processing single story bundle, sequence number: $sequenceNumber")
          processBundle(payload.data.get, sequenceNumber)
        } else {
          logger.info(s"Payload does not contain expected data, sequence number: $sequenceNumber")
          deadLetterQueue.sendMessage(Json.toJson(payload))
        }
      })
    })
  }

  private def processBundle(octopusBundle: OctopusBundle, sequenceNumber: String): Unit = {
    val stream = new Kinesis(Config.apply)
    val cloudWatch = new CloudWatch(Config.apply)

    if (isValidBundle(octopusBundle)) {
      Try(octopusBundle.as[StoryBundle]) match {
        case Success(bundle) =>
          logger.info(s"Bundle passed validation, sequence number: $sequenceNumber")
          val serializedThriftBundle = serializeToBytes(bundle)
          stream.publish(serializedThriftBundle)
        case Failure(e) =>
          logger.info(
            s"Bundle failed validation as StoryBundle, sequence number: $sequenceNumber, with error: ${e}")
          deadLetterQueue.sendMessage(Json.toJson(octopusBundle))
      }
    } else {
      logger.info(s"Bundle failed validation, sequence number: $sequenceNumber")
      cloudWatch.publishMetricEvent()
    }
  }

}
