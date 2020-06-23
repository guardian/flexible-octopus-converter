package com.gu.octopusthrift.services

import play.api.libs.json._
import scala.util.{ Try, Success }
import com.gu.octopusthrift.services.ArticleFinder
import com.gu.octopusthrift.services.Logging
import com.gu.octopusthrift.models.OctopusBundleCache

object PayloadValidator extends Logging {

  def validatePayload(decodedData: Array[Byte]): Option[JsValue] = {
    Try(Json.parse(decodedData)) match {
      case Success(payload) => Some(payload)
      case _ => None
    }
  }

  def getBundleOrBundleCache(json: JsValue): Either[JsValue, OctopusBundleCache] = {
    (json).validate[OctopusBundleCache] match {
      case JsSuccess(bundleCache, _) => Right(bundleCache)
      case _: JsError => Left(json)
    }
  }

  def isValidBundle(json: JsValue): Boolean = {
    hasComposerId(json) && ArticleFinder.findBodyText(json).isDefined
  }

  private def hasComposerId(json: JsValue): Boolean = {
    // the info8 value contains 10 comma-separated values, where the Composer ID is at index 7
    val composerIdLocation = 7

    Try((json \ "info8").get.as[JsString].value.split(',')(composerIdLocation)) match {
      case Success(composerId) => composerId.trim.length > 0
      case _ => {
        logger.info("composer ID not found")
        false
      }
    }
  }

}
