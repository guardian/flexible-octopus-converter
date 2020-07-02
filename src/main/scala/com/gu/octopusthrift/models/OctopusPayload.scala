package com.gu.octopusthrift.models

import play.api.libs.functional.syntax._
import play.api.libs.json._
case class OctopusBundleCacheData(
  bundles: Option[Array[OctopusBundle]],
  thismessageindex: Option[Int],
  totalmessages: Option[Int])
object OctopusBundleCacheData {
  implicit val bundleCacheDataFormat = Json.format[OctopusBundleCacheData]
}

/**
 * The following models represent the possible payload shapes we can receive from Octopus
 *  - the OctopusBundleCachePayload represents a daily snapshot of Octopus data, containing multiple story bundles
 *  - the OctopusSingleBundlePayload represents an update to a single story bundle
 */
case class OctopusBundleCachePayload(`type`: String, data: Option[OctopusBundleCacheData])
object OctopusBundleCachePayload {

  val typeReads = Reads.StringReads.filter(str => {
    str.matches("bundlecache")
  })

  implicit val reads: Reads[OctopusBundleCachePayload] =
    ((__ \ "type").read[String](typeReads) and (__ \ "data")
      .readNullable[OctopusBundleCacheData])(OctopusBundleCachePayload.apply _)

  implicit val writes: Writes[OctopusBundleCachePayload] =
    ((__ \ "type").write[String] and (__ \ "data").writeNullable[OctopusBundleCacheData])(
      unlift(OctopusBundleCachePayload.unapply))

}

case class OctopusSingleBundlePayload(`type`: String, data: Option[OctopusBundle])
object OctopusSingleBundlePayload {
  implicit val singleBundleFormat = Json.format[OctopusSingleBundlePayload]
}
