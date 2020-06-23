package com.gu.octopusthrift.services

import play.api.libs.json._
import com.gu.octopusthrift.models.OctopusArticle
import com.gu.octopusthrift.services.Logging

object ArticleFinder extends Logging {

  def findBodyText(json: JsValue): Option[OctopusArticle] = {
    val bodyTexts = getArticles(json).map(_.filter(article => hasBodyTextForWebPublication(article)))
    bodyTexts.flatMap(getPriorityBodyText)
  }

  private val bodyTextObjectTypes =
    Seq("Body Text", "Tabular Text", "Panel Text")
  private val forWebCodes = Seq("w", "b")

  private def getArticles(json: JsValue): Option[Seq[OctopusArticle]] = {
    val octopusArticles = (json \ "articles").validate[Seq[OctopusArticle]]
    octopusArticles match {
      case JsSuccess(articles, _) => Some(articles)
      case e: JsError => {
        logger.info(s"Unable to find articles, ${JsError.toJson(e)}")
        None
      }
    }

  }

  private def hasBodyTextForWebPublication(article: OctopusArticle): Boolean = {
    bodyTextObjectTypes.contains(article.objectType) && forWebCodes.contains(
      article.forPublication.toLowerCase()) && article.objectNumber == 1 // we want the first of any given object type
  }

  // Where we have more than one type of body text object, the 'Body Text' takes precedence
  private def getPriorityBodyText(bodyTexts: Seq[OctopusArticle]): Option[OctopusArticle] = {
    val bodyText = bodyTexts.find(_.objectType == "Body Text")

    bodyText match {
      case Some(_) => bodyText
      case _ if !bodyTexts.isEmpty => Some(bodyTexts.head)
      case _ => None
    }

  }

}
