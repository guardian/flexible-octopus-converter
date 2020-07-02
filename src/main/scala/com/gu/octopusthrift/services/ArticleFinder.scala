package com.gu.octopusthrift.services

import com.gu.octopusthrift.models._

object ArticleFinder extends Logging {

  def findBodyText(bundle: OctopusBundle): Option[OctopusArticle] = {
    val bodyTexts: Array[OctopusArticle] =
      bundle.articles.filter(hasBodyTextForWebPublication)
    getPriorityBodyText(bodyTexts)
  }

  private val bodyTextObjectTypes =
    Seq("Body Text", "Tabular Text", "Panel Text")
  private val forWebCodes = Seq("w", "b")

  private def hasBodyTextForWebPublication(article: OctopusArticle): Boolean = {
    bodyTextObjectTypes.contains(article.object_type) && forWebCodes.contains(
      article.for_publication.toLowerCase()) && article.object_number == 1 // we want the first of any given object type
  }

  // Where we have more than one type of body text object, the 'Body Text' takes precedence
  private def getPriorityBodyText(bodyTexts: Array[OctopusArticle]): Option[OctopusArticle] = {
    val bodyText = bodyTexts.find(_.object_type == "Body Text")

    bodyText match {
      case Some(_) => bodyText
      case _ if !bodyTexts.isEmpty => Some(bodyTexts.head)
      case _ => None
    }
  }

}
