package com.gu.octopusthrift.services

import play.api.libs.json._
import scala.util.{ Try, Success }
import com.gu.octopusthrift.services.ArticleFinder
import com.gu.octopusthrift.services.Logging
import com.gu.octopusthrift.models._

object PayloadValidator extends Logging {

  def validatePayload(decodedData: Array[Byte]): Option[OctopusPayload] = {
    Try(Json.parse(decodedData)) match {
      case Success(json) =>
        (json).validate[OctopusPayload] match {
          case JsSuccess(payload, _) => Some(payload)
          case _: JsError => None
        }
      case _ => None
    }
  }

  def isValidBundle(bundle: OctopusBundle): Boolean = {
    logger.info(s"Validating bundle: $bundle")
    logger.info(s"Composer ID: ${bundle.composerId}")
    bundle.articles.map(article => logger.info(s"Article: $article"))
    logger.info(s"Body text: ${ArticleFinder.findBodyText(bundle)}")
    bundle.composerId.map(id => id.length > 0).getOrElse(false) && ArticleFinder
      .findBodyText(bundle)
      .isDefined
  }
}
