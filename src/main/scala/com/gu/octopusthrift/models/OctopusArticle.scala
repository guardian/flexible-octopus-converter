package com.gu.octopusthrift.models

import play.api.libs.json._
import play.api.libs.functional.syntax._
import com.gu.flexibleoctopus.model.thrift._
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

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
  isCheckedOut: String,
  status: String,
  attachedTo: Option[Int],
  onPages: Option[String]) {
  def lastModifiedEpoch = DateTime.parse(lastModified, DateTimeFormat.forPattern("yyyyMMddHHmm")).getMillis
  def pageNumber = onPages.map(_.split(',')(0).split(';')(0).toLong)
  def as[T](implicit f: OctopusArticle => T) = f(this)
}

object OctopusArticle {

  implicit val reads = ((__ \ "id").read[Int] and
    (__ \ "filename").read[String] and
    (__ \ "for_publication").read[String] and
    (__ \ "lawyered").read[String] and
    (__ \ "object_type").read[String] and
    (__ \ "object_number").read[Int] and
    (__ \ "last_modified").read[String] and
    (__ \ "in_use_by").readNullable[String] and
    (__ \ "ischeckedout").read[String] and
    (__ \ "status").read[String] and
    (__ \ "attached_to").readNullable[Int] and
    (__ \ "on_pages").readNullable[String])(OctopusArticle.apply _)

  implicit def articleMapper = (octopusArticle: OctopusArticle) => {

    val forPub = Map("w" -> ForPublication.Web, "b" -> ForPublication.Both)

    val lawyered = Map(
      "n" -> Lawyered.Notapplicable,
      "p" -> Lawyered.Pending,
      "c" -> Lawyered.Cleared,
      "r" -> Lawyered.Priority)

    val articleStatus = Map(
      "Chief Sub" -> ArticleStatus.Chiefsub,
      "Desk" -> ArticleStatus.Desk,
      "Finalled" -> ArticleStatus.Finalled,
      "Hold" -> ArticleStatus.Hold,
      "Killed" -> ArticleStatus.Killed,
      "Revise Sub" -> ArticleStatus.Revisesub,
      "Subs" -> ArticleStatus.Subs,
      "Writers" -> ArticleStatus.Writers)

    val isCheckedOut = Map("Y" -> true, "N" -> false)

    Article(
      octopusArticle.id,
      forPub(octopusArticle.forPublication),
      octopusArticle.inUseBy,
      isCheckedOut(octopusArticle.isCheckedOut),
      octopusArticle.lastModifiedEpoch,
      lawyered(octopusArticle.lawyered),
      articleStatus(octopusArticle.status),
      octopusArticle.filename)
  }
}
