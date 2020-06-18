package com.gu.octopusthrift

import com.amazonaws.services.lambda.runtime.Context
import play.api.libs.json._
import com.gu.octopusthrift.aws.Kinesis
import com.gu.octopusthrift.services.Logging

/**
 * This is compatible with aws' lambda JSON to POJO conversion.
 * You can test your lambda by sending it the following payload:
 * {"name": "Bob"}
 */
class LambdaInput() {
  var name: String = _
  def getName(): String = name
  def setName(theName: String): Unit = name = theName
}

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
  def handler(lambdaInput: JsValue, context: Context): Unit = {
    val env = Env()
    logger.info(s"Starting $env")
    logger.info(s"Received: \n $lambdaInput")
    val stream = new Kinesis()
    stream.publish(lambdaInput)
  }

  /*
   * I recommend to put your logic outside of the handler
   */
  def process(json: String): JsValue = Json.parse(json)
}

object TestIt {
  def main(args: Array[String]): Unit = {
    println(Lambda.process(args.headOption.getOrElse("Alex")))
  }
}
