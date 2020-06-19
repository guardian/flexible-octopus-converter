package com.gu.octopusthrift.services

import play.api.libs.json._

object ArticleFinder {

  def findBodyText(json: JsValue): Option[JsObject] = {
    getArticles(json).flatMap(_.find(hasBodyTextForWebPublication))
  }

  private val bodyTextObjectTypes =
    Seq("Body Text", "Tabular Text", "Panel Text")
  private val forWebCodes = Seq("w", "b")

  private def getArticles(json: JsValue): Option[Seq[JsObject]] = {
    Some((json \ "articles").get.as[Seq[JsObject]])
  }

  private def extractJsonString(article: JsObject, property: String) = {
    (article \ property).get.as[JsString].value
  }

  private def hasBodyTextForWebPublication(article: JsObject): Boolean = {
    val objectType = (article \ "object_type")
    val forPublicationValue = (article \ "for_publication")

    (objectType.isDefined, forPublicationValue.isDefined) match {
      case (true, true) => {
        val objectType = extractJsonString(article, "object_type").split('[')(0).trim()
        val forPublicationValue = extractJsonString(article, "for_publication").toLowerCase().trim()
        bodyTextObjectTypes.contains(objectType) && forWebCodes.contains(forPublicationValue)
      }
      case _ => false
    }
  }

}
