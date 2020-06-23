package com.gu.octopusthrift

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.KinesisEvent
import com.amazonaws.services.kinesis.model.Record
import play.api.libs.json._
import com.gu.octopusthrift.aws.Kinesis
import com.gu.octopusthrift.services.Logging
import com.gu.octopusthrift.Config
import com.gu.octopusthrift.models._
import com.gu.octopusthrift.services._
import com.gu.octopusthrift.services.PayloadValidator.{ getBundleOrBundleCache, isValidBundle, validatePayload }
import java.nio.ByteBuffer
import java.util.Base64
import scala.jdk.CollectionConverters._
import scala.util.{ Success, Try }

object Lambda extends Logging {

  def handler(lambdaInput: KinesisEvent, context: Context): Unit = {
    val records: List[Record] = lambdaInput.getRecords.asScala.map(_.getKinesis).toList

    records.map(record => {
      val data = record.getData().array()
      val validatedPayload = validatePayload(Base64.getDecoder().decode(data))
      val bundleOrBundleCache = validatedPayload.map(getBundleOrBundleCache)

      bundleOrBundleCache map {
        case Left(singleBundle: OctopusBundle) => processBundle(singleBundle, record.getSequenceNumber)
        case Right(bundleCache: OctopusBundleCache) => {
          logger.info("Processing daily snapshot of all live story bundles in Octopus database")
          bundleCache.bundles.map(bundle => processBundle(bundle, record.getSequenceNumber))
        }
      }
    })
  }

  private def processBundle(bundle: OctopusBundle, sequenceNumber: String): Unit = {
    if (isValidBundle(bundle)) {
      // TODO: parse to thrift model, base64 encode, push to stream
      val stream = new Kinesis(Config.apply)
      // stream.publish(json)
    } else {
      logger.info(s"JSON paylod is invalid, sequence number: $sequenceNumber")
      // TODO: dead letter queue
    }
  }

}
