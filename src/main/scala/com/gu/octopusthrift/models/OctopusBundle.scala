package com.gu.octopusthrift.models

import java.util.concurrent.TimeUnit

import com.gu.flexibleoctopus.model.thrift._
import com.gu.octopusthrift.services.ArticleFinder
import org.joda.time.format.DateTimeFormat
import org.joda.time.{ DateTime, DateTimeZone }
import play.api.libs.functional.syntax._
import play.api.libs.json._

import scala.util.{ Try, Success }

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

  def composerId: Option[String] = {
    val i8 = info8.split(',')
    val possibleComposerId = Try(i8(composerIdLocation))
    (i8.length > 7, possibleComposerId) match {
      case (true, Success(id)) => if (id.trim.length > 0) Some(id.trim) else None
      case _ => None
    }
  }

  def pubDateEpochDays: Option[Long] =
    pubDate.map(date =>
      TimeUnit.MILLISECONDS.toDays(
        DateTime
          .parse(date, DateTimeFormat.forPattern("yyyyMMdd").withZoneUTC())
          .withZone(DateTimeZone.UTC)
          .getMillis))

  def as[T](implicit f: OctopusBundle => T): T = f(this)
}

object OctopusBundle {
  implicit val reads: Reads[OctopusBundle] = ((__ \ "id").read[Int] and
    (__ \ "info8").read[String] and
    (__ \ "pubcode").read[String] and
    (__ \ "pubdate").readNullable[String] and
    (__ \ "sectioncode").read[String] and
    (__ \ "articles").read[Array[OctopusArticle]])(OctopusBundle.apply _)

  implicit val writes: Writes[OctopusBundle] = ((__ \ "id").write[Int] and
    (__ \ "info8").write[String] and
    (__ \ "pubCode").write[String] and
    (__ \ "pubDate").writeNullable[String] and
    (__ \ "sectionCode").write[String] and
    (__ \ "articles").write[Array[OctopusArticle]])(unlift(OctopusBundle.unapply))

  implicit def bundleMapper: OctopusBundle => StoryBundle = (octopusBundle: OctopusBundle) => {

    val bodyText = ArticleFinder.findBodyText(octopusBundle)
    val article = bodyText.get.as[Article]
    val headline = None

    StoryBundle(
      octopusBundle.id,
      octopusBundle.composerId.getOrElse(""),
      octopusBundle.pubCode,
      octopusBundle.sectionCode,
      article,
      headline,
      bodyText.flatMap(_.pageNumber),
      octopusBundle.pubDateEpochDays,
      bodyText.flatMap(_.attachedTo.map(_.toString)))
  }
}
