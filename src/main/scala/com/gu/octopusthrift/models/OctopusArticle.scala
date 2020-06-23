package com.gu.octopusthrift.models

import play.api.libs.json._

import play.api.libs.functional.syntax._

/**
 * This model does not represent all fields sent by Octopus,
 * only those that will be relevant in converting to our Thrift model
 */
case class OctopusArticle(
  id: Int,
  filename: String,
  forPublication: String,
  lawyered: String,
  objectType: String,
  objectNumber: Int,
  lastModified: String,
  inUseBy: Option[String],
  isCheckedOut: String)

object OctopusArticle {
  implicit val reads = ((__ \ "id").read[Int] and
    (__ \ "filename").read[String] and
    (__ \ "for_publication").read[String] and
    (__ \ "lawyered").read[String] and
    (__ \ "object_type").read[String] and
    (__ \ "object_number").read[Int] and
    (__ \ "last_modified").read[String] and
    (__ \ "in_use_by").readNullable[String] and
    (__ \ "ischeckedout").read[String])(OctopusArticle.apply _)
}
