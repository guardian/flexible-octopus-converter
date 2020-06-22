package com.gu.octopusthrift

import com.amazonaws.services.lambda.runtime.Context
import play.api.libs.json._
import com.gu.octopusthrift.aws.Kinesis
import com.gu.octopusthrift.services.Logging
import java.io.InputStream
import com.amazonaws.services.lambda.runtime.events.KinesisEvent
import com.amazonaws.services.kinesis.model.Record
import scala.jdk.CollectionConverters._
import com.gu.octopusthrift.services._
import java.nio.ByteBuffer

object Lambda extends Logging {

  def handler(lambdaInput: KinesisEvent, context: Context): Unit = {
    val records: List[Record] = lambdaInput.getRecords.asScala.map(_.getKinesis).toList
    records.map(process)
  }

  def process(record: Record): Unit = {
    // extract the data
    val data: Array[Byte] = record.getData().array()
    // check is json and has composer ID and body text
    val isValid = PayloadValidator.isValidPayload(data)

    if (!isValid) {
      logger.info(s"JSON paylod is invalid, sequence number: ${record.getSequenceNumber}")
      // TODO: dead letter queue
    } else {
      val json: JsValue = Json.parse(data)
      val stream = new Kinesis()
      stream.publish(json)
    }
  }

}