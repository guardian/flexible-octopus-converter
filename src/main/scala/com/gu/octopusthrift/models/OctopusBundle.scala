package com.gu.octopusthrift.models

import play.api.libs.json._
import play.api.libs.functional.syntax._
import com.gu.octopusthrift.models.OctopusArticle
import com.gu.flexibleoctopus.model.thrift._
import com.gu.octopusthrift.services.ArticleFinder
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

/**
 * This model does not represent all fields sent by Octopus,
 * only those that will be relevant in converting to our Thrift model
 */
case class OctopusBundle(
  id: Int,
  info8: String,
  pubCode: String,
  pubDate: Option[String],
  sectionCode: String,
  articles: Array[OctopusArticle]) {
  private val composerIdLocation = 7
  def composerId: String = info8.split(',')(composerIdLocation).trim
  def pubDateEpochDays = pubDate.map(date => DateTime.parse(date, DateTimeFormat.forPattern("yyyyMMdd")).getMillis)
  def as[T](implicit f: OctopusBundle => T) = f(this)
}

object OctopusBundle {
  implicit val reads = ((__ \ "id").read[Int] and
    (__ \ "info8").read[String] and
    (__ \ "pubcode").read[String] and
    (__ \ "pubdate").readNullable[String] and
    (__ \ "sectioncode").read[String] and
    (__ \ "articles").read[Array[OctopusArticle]])(OctopusBundle.apply _)

  implicit def bundleMapper = (octopusBundle: OctopusBundle) => {

    val bodyText = ArticleFinder.findBodyText(octopusBundle)
    val article = bodyText.get.as[Article]
    val headline = None

    StoryBundle(
      octopusBundle.id,
      octopusBundle.composerId,
      octopusBundle.pubCode,
      octopusBundle.sectionCode,
      article,
      headline,
      bodyText.flatMap(_.pageNumber),
      octopusBundle.pubDateEpochDays,
      bodyText.flatMap(_.attachedTo.map(_.toString)))
  }
}
