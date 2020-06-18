package com.gu.octopusthrift

import com.amazonaws.services.lambda.runtime.Context
import play.api.libs.json._
import com.gu.octopusthrift.aws.Kinesis
import com.gu.octopusthrift.services.Logging
import java.io.InputStream
import com.amazonaws.services.lambda.runtime.events.KinesisEvent
import com.amazonaws.services.kinesis.model.Record
import scala.jdk.CollectionConverters._

object Lambda extends Logging {
  /*
   * This is your lambda entry point
   */
  def handler(lambdaInput: KinesisEvent, context: Context): Unit = {

    val records: List[Record] = lambdaInput.getRecords.asScala.map(_.getKinesis).toList
    val stream = new Kinesis()
    records.map(record => stream.publish(Json.parse(record.getData().array())))
  }

  /*
   * I recommend to put your logic outside of the handler
   */
  def process(json: String): JsValue = Json.parse(json)
}

