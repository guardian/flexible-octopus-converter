package models

import org.scalatest.wordspec.AnyWordSpec

import com.gu.octopusthrift.models.OctopusArticle
import com.gu.flexibleoctopus.model.thrift._

object ArticleTestHelpers {
  def createOctopusArticleWithPage(onPages: Option[String]) =
    OctopusArticle(
      1234,
      "article.t0",
      "w",
      "n",
      "Body Text",
      1,
      "202006241200",
      None,
      "N",
      "Writers",
      Some(1000),
      onPages,
      "Test User")
}

class OctopusArticleSpec extends AnyWordSpec {
  "OctopusArticle" when {
    "as[Article] is called" should {
      "map the values correctly" in {
        val octopusArticle = OctopusArticle(
          1234,
          "article.t0",
          "w",
          "n",
          "Body Text",
          1,
          "202006241200",
          None,
          "N",
          "Writers",
          Some(1000),
          Some("1"),
          "Test User")

        val thriftArticle = octopusArticle.as[Article]

        assert(thriftArticle.forPublication == ForPublication.Web)
        assert(thriftArticle.isCheckedOut == false)
        assert(thriftArticle.lastModified == 1593000000)
        assert(thriftArticle.lawyered == Lawyered.Notapplicable)
        assert(thriftArticle.status == ArticleStatus.Writers)
        assert(thriftArticle.lastModifiedBy == "Test User")
      }
    }
  }
}
