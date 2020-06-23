package scala.services

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.Json

import com.gu.octopusthrift.services.ArticleFinder
import com.gu.octopusthrift.models.OctopusArticle
import scala.TestUtils

class ArticleFinderSpec extends AnyWordSpec with Matchers {

  "ArticleFinder" when {
    "findBodyText is called" should {
      "retrieve the body text when it's present" in {
        val exampleJson = TestUtils.readJson(s"/example.json")
        val expectedArticle = TestUtils.readJson(s"/article.json").as[OctopusArticle]
        assert(ArticleFinder.findBodyText(exampleJson) == Some(expectedArticle))
      }
      "return None when there's no body text found" in {
        val exampleJson = TestUtils.readJson(s"/exampleWithoutBodyText.json")
        assert(ArticleFinder.findBodyText(exampleJson) == None)
      }
      "return None when there is a body text, but it isn't for web" in {
        val exampleJson = TestUtils.readJson(s"/exampleWithNoWebPublication.json")
        assert(ArticleFinder.findBodyText(exampleJson) == None)
      }
      "retrieve the primary body text when there is more than one possible body text" in {
        val articleWithMultipleBodyTexts = TestUtils.readJson(s"/exampleWithMultipleBodyTexts.json")
        val articleWithBodyPanelAndTabularTexts =
          TestUtils.readJson(s"/exampleWithMultipleBodyPanelAndTabular.json")
        val expectedArticle = TestUtils.readJson(s"/article.json").as[OctopusArticle]

        assert(ArticleFinder.findBodyText(articleWithMultipleBodyTexts) == Some(expectedArticle))
        assert(ArticleFinder.findBodyText(articleWithBodyPanelAndTabularTexts) == Some(expectedArticle))
      }
    }
  }

}
