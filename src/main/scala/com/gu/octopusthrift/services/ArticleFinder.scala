package com.gu.octopusthrift.services

import play.api.libs.json._

object ArticleFinder {

  def findBodyText(json: JsValue): Option[JsObject] = {
    val bodyTexts: Option[Seq[JsObject]] = getArticles(json).map(_.filter(hasBodyTextForWebPublication))
    bodyTexts.flatMap(getPriorityBodyText)
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

  private def extractJsonNumber(article: JsObject, property: String) = {
    (article \ property).get.as[JsNumber].value
  }

  private def hasBodyTextForWebPublication(article: JsObject): Boolean = {
    val objectType = (article \ "object_type")
    val forPublicationValue = (article \ "for_publication")

    (objectType.isDefined, forPublicationValue.isDefined) match {
      case (true, true) => {
        val objectType = extractJsonString(article, "object_type").split('[')(0).trim()
        val objectNumber = extractJsonNumber(article, "object_number")
        val forPublicationValue = extractJsonString(article, "for_publication").toLowerCase().trim()
        bodyTextObjectTypes.contains(objectType) && forWebCodes.contains(forPublicationValue) && objectNumber == 1
      }
      case _ => false
    }
  }

  private def getPriorityBodyText(bodyTexts: Seq[JsObject]): Option[JsObject] = {
    val bodyText = bodyTexts.find(bt => extractJsonString(bt, "object_type") == "Body Text")

    (bodyText, bodyTexts.isEmpty) match {
      case (Some(value), _) => bodyText
      case (None, false) => Some(bodyTexts.head)
      case _ => None
    }

  }

}
