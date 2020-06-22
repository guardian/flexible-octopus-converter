package scala.services

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.Json
import scala.io.Source

import com.gu.octopusthrift.services.ArticleFinder

class ArticleFinderSpec extends AnyWordSpec with Matchers {

  private def readJson(path: String) = {
    val resource = getClass.getResourceAsStream(path)
    Json.parse(Source.fromInputStream(resource).getLines().mkString)
  }

  "ArticleFinder" when {
    "findBodyText is called" should {
      "retrieve the body text when it's present" in {
        val exampleJson = readJson(s"/example.json")
        val expectedArticle = readJson(s"/article.json")
        assert(ArticleFinder.findBodyText(exampleJson) == Some(expectedArticle))
      }
      "return None when there's no body text found" in {
        val exampleJson = readJson(s"/exampleWithoutBodyText.json")
        assert(ArticleFinder.findBodyText(exampleJson) == None)
      }
      "return None when there is a body text, but it isn't for web" in {
        val exampleJson = readJson(s"/exampleWithNoWebPublication.json")
        assert(ArticleFinder.findBodyText(exampleJson) == None)
      }
      "retrieve the primary body text when there is more than one possible body text" in {
        val articleWithMultipleBodyTexts = readJson(s"/exampleWithMultipleBodyTexts.json")
        val articleWithBodyPanelAndTabularTexts = readJson(s"/exampleWithMultipleBodyPanelAndTabular.json")
        val expectedArticle = readJson(s"/article.json")

        assert(ArticleFinder.findBodyText(articleWithMultipleBodyTexts) == Some(expectedArticle))
        assert(ArticleFinder.findBodyText(articleWithBodyPanelAndTabularTexts) == Some(expectedArticle))
      }
    }
  }

}
