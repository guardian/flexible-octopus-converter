package com.gu.octopusthrift.models

import play.api.libs.functional.syntax._
import play.api.libs.json._

/** This model represents part of the payload received from Octopus */
case class OctopusPayload(
  `type`: String,
  bundles: Option[Array[OctopusBundle]],
  data: Option[OctopusBundle],
  thismessageindex: Option[Int],
  totalmessages: Option[Int])

object OctopusPayload {
  implicit val reads: Reads[OctopusPayload] =
    ((__ \ "type").read[String] and (__ \ "bundles").readNullable[Array[OctopusBundle]] and (__ \ "data")
      .readNullable[OctopusBundle] and (__ \ "thismessageindex").readNullable[Int] and (__ \ "totalmessages")
      .readNullable[Int])(OctopusPayload.apply _)

  implicit val writes: Writes[OctopusPayload] =
    ((__ \ "type").write[String] and (__ \ "bundles").writeNullable[Array[OctopusBundle]] and (__ \ "data")
      .writeNullable[OctopusBundle] and (__ \ "thismessageindex")
      .writeNullable[Int] and (__ \ "totalmessages").writeNullable[Int])(unlift(OctopusPayload.unapply))
}
