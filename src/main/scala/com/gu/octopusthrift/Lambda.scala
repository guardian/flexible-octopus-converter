package com.gu.octopusthrift

import com.amazonaws.services.lambda.runtime.Context
import play.api.libs.json._
import com.gu.octopusthrift.aws.Kinesis
import com.gu.octopusthrift.services.Logging
import java.io.InputStream
import com.amazonaws.services.lambda.runtime.events.KinesisEvent
import com.amazonaws.services.kinesis.model.Record
import scala.collection.JavaConverters._

case class Env(app: String, stack: String, stage: String) {
  override def toString: String = s"App: $app, Stack: $stack, Stage: $stage\n"
}

object Env {
  def apply(): Env = Env(
    Option(System.getenv("App")).getOrElse("DEV"),
    Option(System.getenv("Stack")).getOrElse("DEV"),
    Option(System.getenv("Stage")).getOrElse("DEV"))
}

object Lambda extends Logging {
  /*
   * This is your lambda entry point
   */
  def handler(lambdaInput: KinesisEvent, context: Context): Unit = {

    val records: List[Record] = lambdaInput.getRecords.asScala.map(_.getKinesis).toList
    val env = Env()
    val stream = new Kinesis()
    records.map(record => stream.publish(Json.parse(record.getData().array())))

    // try {
    //   stream.publish(Json.parse(lambdaInput))
    // } catch {
    //   case e: Exception =>
    //     logger.info("Couldn't parse Json")
    // }
  }

  /*
   * I recommend to put your logic outside of the handler
   */
  def process(json: String): JsValue = Json.parse(json)
}

