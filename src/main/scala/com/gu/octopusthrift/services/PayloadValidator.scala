package com.gu.octopusthrift.services

import play.api.libs.json._
import scala.util.{ Try, Success }
import com.gu.octopusthrift.services.ArticleFinder
import com.gu.octopusthrift.services.Logging
import com.gu.octopusthrift.models._

object PayloadValidator extends Logging {

  def validatePayload(decodedData: Array[Byte]): Option[JsValue] = {
    Try(Json.parse(decodedData)) match {
      case Success(payload) => Some(payload)
      case _ => None
    }
  }

  def getBundleOrBundleCache(json: JsValue): Either[OctopusBundle, OctopusBundleCache] = {
    (json).validate[OctopusBundleCache] match {
      case JsSuccess(bundleCache, _) => Right(bundleCache)
      case _: JsError => Left(json.as[OctopusBundle])
    }
  }

  def isValidBundle(bundle: OctopusBundle): Boolean = {
    bundle.composerId.length > 0 && ArticleFinder.findBodyText(bundle).isDefined
  }
}
