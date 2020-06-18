package com.gu.octopusthrift.services

import play.api.libs.json._

object ArticleFinder {
  def findBodyText(json: JsValue): JsObject = {
    val articles = getArticles(json)
    val bodyText = getBodyText(articles)
    bodyText
  }

  private val bodyTextObjectTypes = Seq("Body Text", "Tabular Text", "Panel Text")
  private val forWebCodes = Seq("w", "W", "b", "B")

  private def getArticles(json: JsValue): Seq[JsObject] = {
    (json \ "articles").get.as[Seq[JsObject]]
  }

  private def getBodyText(articles: Seq[JsObject]): JsObject = {

    var bodyTextArticles = Seq[JsObject]()

    for (article <- articles) {
      val objectType = (article \ "objectType").get.as[JsString].value.split('[')(0)
      val forPublicationValue = (article \ "for_publication").get.as[JsString]

      if (bodyTextObjectTypes.contains(objectType) && forWebCodes.contains(forPublicationValue))
        bodyTextArticles = bodyTextArticles :+ article
    }

    if (bodyTextArticles.length != 1)
      throw new Exception("Too many body text articles found")

    bodyTextArticles(0)
  }

}