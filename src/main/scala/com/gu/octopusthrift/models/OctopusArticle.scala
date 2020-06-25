package com.gu.octopusthrift.models

import java.util.concurrent.TimeUnit

import play.api.libs.json._
import play.api.libs.functional.syntax._
import com.gu.flexibleoctopus.model.thrift._
import org.joda.time.{ DateTime, DateTimeZone }
import org.joda.time.format.DateTimeFormat
import scala.util.{ Try, Success }

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

  def lastModifiedEpoch =
    TimeUnit.MILLISECONDS.toSeconds(
      DateTime
        .parse(lastModified, DateTimeFormat.forPattern("yyyyMMddHHmm").withZoneUTC())
        .withZone(DateTimeZone.UTC)
        .getMillis)

  def pageNumber: Option[Long] = {
    Try(onPages.map(_.split(',')(0).split(';')(0).toLong)) match {
      case Success(number) => number
      case _ => None
    }
  }

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

  implicit val writes = ((__ \ "id").write[Int] and
    (__ \ "filename").write[String] and
    (__ \ "for_publication").write[String] and
    (__ \ "lawyered").write[String] and
    (__ \ "object_type").write[String] and
    (__ \ "object_number").write[Int] and
    (__ \ "last_modified").write[String] and
    (__ \ "in_use_by").writeNullable[String] and
    (__ \ "ischeckedout").write[String] and
    (__ \ "status").write[String] and
    (__ \ "attached_to").writeNullable[Int] and
    (__ \ "on_pages").writeNullable[String])(unlift(OctopusArticle.unapply))

  implicit def articleMapper = (octopusArticle: OctopusArticle) => {

    val forPub = Map("w" -> ForPublication.Web, "b" -> ForPublication.Both)

    val lawyered = Map(
      "n" -> Lawyered.Notapplicable,
      "p" -> Lawyered.Pending,
      "c" -> Lawyered.Cleared,
      "r" -> Lawyered.Priority)

    val articleStatus = Map(
      "chief sub" -> ArticleStatus.Chiefsub,
      "desk" -> ArticleStatus.Desk,
      "finalled" -> ArticleStatus.Finalled,
      "hold" -> ArticleStatus.Hold,
      "killed" -> ArticleStatus.Killed,
      "revise sub" -> ArticleStatus.Revisesub,
      "subs" -> ArticleStatus.Subs,
      "writers" -> ArticleStatus.Writers)

    val isCheckedOut = Map("y" -> true, "n" -> false)

    Article(
      octopusArticle.id,
      forPub(octopusArticle.forPublication.toLowerCase),
      octopusArticle.inUseBy,
      isCheckedOut(octopusArticle.isCheckedOut.toLowerCase),
      octopusArticle.lastModifiedEpoch,
      lawyered(octopusArticle.lawyered.toLowerCase),
      articleStatus(octopusArticle.status.toLowerCase),
      octopusArticle.filename)
  }
}
