package com.gu.octopusthrift.models

import play.api.libs.json._

import play.api.libs.functional.syntax._

import com.gu.octopusthrift.models.OctopusBundle

/** This model represents a daily snapshot of all live story bundles in the Octopus database */
case class OctopusBundleCache(
  totalMessages: Int,
  thisMessageIndex: Int,
  bundles: Array[OctopusBundle],
  `type`: String)

object OctopusBundleCache {
  implicit val reads = ((__ \ "totalmessages").read[Int] and
    (__ \ "thismessageindex").read[Int] and
    (__ \ "bundles").read[Array[OctopusBundle]] and
    (__ \ "type").read[String])(OctopusBundleCache.apply _)
}
