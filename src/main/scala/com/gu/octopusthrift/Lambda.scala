package com.gu.octopusthrift

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.KinesisEvent
import com.amazonaws.services.kinesis.model.Record
import play.api.libs.json._
import com.gu.octopusthrift.aws.{ Kinesis, SQS }
import com.gu.octopusthrift.services.Logging
import com.gu.octopusthrift.Config
import com.gu.octopusthrift.models._
import com.gu.octopusthrift.services.PayloadValidator.{
  getBundleOrBundleCache,
  isValidBundle,
  validatePayload
}
import scala.jdk.CollectionConverters._
import com.gu.flexibleoctopus.model.thrift._
import com.gu.octopusthrift.util.ThriftSerializer.{ serializeToBytes }
import scala.util.{ Try, Success }

object Lambda extends Logging {

  def handler(lambdaInput: KinesisEvent, context: Context): Unit = {
    val records: List[Record] = lambdaInput.getRecords.asScala.map(_.getKinesis).toList

    records.map(record => {
      val data = record.getData().array()
      val validatedPayload = validatePayload(data)
      validatedPayload.map(json => logger.info(s"validated json: ${json}"))
      val bundleOrBundleCache = validatedPayload.map(getBundleOrBundleCache)
      val sequenceNumber = record.getSequenceNumber

      bundleOrBundleCache map {
        case Left(bundle: OctopusBundle) => {
          logger.info(s"Processing single story bundle, sequence number: $sequenceNumber")
          processBundle(bundle, sequenceNumber)
        }
        case Right(bundleCache: OctopusBundleCache) => {
          logger.info(
            s"Processing daily snapshot of all live story bundles in Octopus database, sequence number: $sequenceNumber")
          bundleCache.bundles.map(bundle => processBundle(bundle, sequenceNumber))
        }
      }
    })
  }

  private def processBundle(octopusBundle: OctopusBundle, sequenceNumber: String): Unit = {
    val stream = new Kinesis(Config.apply)
    val deadLetterQueue = new SQS(Config.apply)

    if (isValidBundle(octopusBundle)) {
      Try(octopusBundle.as[StoryBundle]) match {
        case Success(bundle) => {
          logger.info(s"Bundle passed validation, sequence number: $sequenceNumber")
          val serializedThriftBundle = serializeToBytes(bundle)
          stream.publish(serializedThriftBundle)
        }
        case _ => {
          logger.info(s"Bundle failed validation as StoryBundle, sequence number: $sequenceNumber")
          deadLetterQueue.sendMessage(Json.toJson(octopusBundle))
        }
      }
    } else {
      logger.info(s"Bundle failed validation, sequence number: $sequenceNumber")
    }
  }

}
