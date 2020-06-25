package com.gu.octopusthrift.models

import play.api.libs.json._

import play.api.libs.functional.syntax._

import com.gu.octopusthrift.models.OctopusBundle

/** This model represents part of the payload received from Octopus */
case class OctopusPayload(`type`: String, bundles: Option[Array[OctopusBundle]], data: Option[OctopusBundle])

object OctopusPayload {
  implicit val reads: Reads[OctopusPayload] =
    ((__ \ "type").read[String] and (__ \ "bundles").readNullable[Array[OctopusBundle]] and (__ \ "data")
      .readNullable[OctopusBundle])(OctopusPayload.apply _)

  implicit val writes: Writes[OctopusPayload] =
    ((__ \ "type").write[String] and (__ \ "bundles").writeNullable[Array[OctopusBundle]] and (__ \ "data")
      .writeNullable[OctopusBundle])(unlift(OctopusPayload.unapply))
}
