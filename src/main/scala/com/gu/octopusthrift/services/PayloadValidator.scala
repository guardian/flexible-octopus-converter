package com.gu.octopusthrift.services

import com.gu.octopusthrift.aws.{ CustomMetrics, DeadLetterQueue, Metrics }
import com.gu.octopusthrift.models._
import play.api.libs.json._

import scala.util.{ Success, Try }

object PayloadValidator extends Logging with CustomMetrics with DeadLetterQueue {

  def validatePayload(decodedData: Array[Byte]): Option[OctopusPayload] = {
    Try(Json.parse(decodedData)) match {
      case Success(json) =>
        json.validate[OctopusPayload] match {
          case JsSuccess(payload, _) => Some(payload)
          case _: JsError => {
            logger.info(s"Payload does not match OctopusPayload model: $json")
            deadLetterQueue.sendMessage(json)
            cloudWatch.publishMetricEvent(Metrics.InvalidOctopusPayload)
            None
          }
        }
      case _ => None
    }
  }

  def isValidBundle(bundle: OctopusBundle): Boolean = {
    logger.info(s"Validating bundle: $bundle")
    bundle.articles.foreach(article => logger.info(s"Article: $article"))
    logger.info(s"Body text: ${ArticleFinder.findBodyText(bundle)}")
    logger.info(s"Composer ID: ${bundle.composerId}")
    val hasComposerId = bundle.composerId.isDefined
    val hasBodyText = ArticleFinder.findBodyText(bundle).isDefined
    hasComposerId && hasBodyText
  }
}
