package com.gu.octopusthrift.models

import play.api.libs.json._

import play.api.libs.functional.syntax._

/** This model represents a daily snapshot of all live story bundles in the Octopus database */
case class OctopusBundleCache(
  totalMessages: Int,
  thisMessageIndex: Int,
  bundles: Array[JsValue],
  `type`: String)

object OctopusBundleCache {
  implicit val reads = ((__ \ "totalmessages").read[Int] and
    (__ \ "thismessageindex").read[Int] and
    (__ \ "bundles").read[Array[JsValue]] and
    (__ \ "type").read[String])(OctopusBundleCache.apply _)
}
