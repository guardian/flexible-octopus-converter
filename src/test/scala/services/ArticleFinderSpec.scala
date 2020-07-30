package services

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

import com.gu.octopusthrift.services.ArticleFinder
import com.gu.octopusthrift.models._

class ArticleFinderSpec extends AnyWordSpec with Matchers {

  def createTestArticle(forPublication: String, objectType: String, objectNumber: Int) =
    OctopusArticle(
      1233,
      "article.t0",
      forPublication,
      "n",
      objectType,
      objectNumber,
      "202006241200",
      None,
      "N",
      "Writers",
      Some(1000),
      Some("1"),
      "Test User"
    )

  def createTestBundle(articles: Array[OctopusArticle]) = OctopusBundle(101, "", "", Some(""), "", articles)

  "ArticleFinder" when {
    "findBodyText is called" should {
      "retrieve the body text when it's present" in {
        val exampleJson = TestUtils.readJson(s"/example.json").as[OctopusBundle]
        val expectedArticle = TestUtils.readJson(s"/article.json").as[OctopusArticle]
        assert(ArticleFinder.findBodyText(exampleJson).contains(expectedArticle))
      }
      "return None when there's no body text found" in {
        val exampleJson = TestUtils.readJson(s"/exampleWithoutBodyText.json").as[OctopusBundle]
        assert(ArticleFinder.findBodyText(exampleJson).isEmpty)
      }
      "return None when there is not a complete article" in {
        val exampleJson = TestUtils.readJson(s"/exampleWithMissingArticleFields.json").as[OctopusBundle]
        assert(ArticleFinder.findBodyText(exampleJson).isEmpty)
      }
      "return None when there is nothing for suitable publication" in {
        val exampleJson = TestUtils.readJson(s"/exampleProblem.json").as[OctopusBundle]
        assert(ArticleFinder.findBodyText(exampleJson).isEmpty)
      }
      "retrieve the primary body text when there is more than one possible body text" in {
        val articleWithMultipleBodyTexts =
          TestUtils.readJson(s"/exampleWithMultipleBodyTexts.json").as[OctopusBundle]
        val articleWithBodyPanelAndTabularTexts =
          TestUtils.readJson(s"/exampleWithMultipleBodyPanelAndTabular.json").as[OctopusBundle]
        val expectedArticle = TestUtils.readJson(s"/article.json").as[OctopusArticle]

        assert(ArticleFinder.findBodyText(articleWithMultipleBodyTexts).contains(expectedArticle))
        assert(ArticleFinder.findBodyText(articleWithBodyPanelAndTabularTexts).contains(expectedArticle))
      }
      "prefer 'for_publication' of both to web and print" in {
        val articleForWeb = createTestArticle("w", "Body Text", 1)
        val articleForPrint = createTestArticle("p", "Body Text", 1)
        val articleForBoth = createTestArticle("b", "Body Text", 1)
        val articles = Array(articleForPrint, articleForWeb, articleForBoth)

        assert(ArticleFinder.findBodyText(createTestBundle(articles)).contains(articleForBoth))
      }
      "prefer 'for_publication' of web to print" in {
        val articleForWeb = createTestArticle("w", "Body Text", 1)
        val articleForPrint = createTestArticle("p", "Body Text", 1)
        val articles = Array(articleForPrint, articleForWeb)

        assert(ArticleFinder.findBodyText(createTestBundle(articles)).contains(articleForWeb))
      }
      "ignore 'for_publication' values of 'n' and 'y'" in {
        val articleOne = createTestArticle("y", "Body Text", 1)
        val articleTwo = createTestArticle("n", "Body Text", 1)
        val articles = Array(articleOne, articleTwo)

        assert(ArticleFinder.findBodyText(createTestBundle(articles)).isEmpty)
      }
      "prefer 'object_type' of Body Text to other types" in {
        val articleObjectOne = createTestArticle("w", "Tabular Text", 1)
        val articleObjectTwo = createTestArticle("w", "Body Text [Ruled]", 1)
        val articleObjectThree = createTestArticle("w", "Panel Text", 1)
        val articleObjectFour = createTestArticle("w", "Headline", 1)
        val articles = Array(articleObjectFour, articleObjectTwo, articleObjectThree, articleObjectOne)

        assert(
          ArticleFinder
            .findBodyText(createTestBundle(articles))
            .contains(articleObjectTwo.copy(object_type = "Body Text"))
        )
      }
      "prefer 'object_type' of Panel Text to Tabular types" in {
        val tabularArticleForWeb = createTestArticle("w", "Tabular Text", 1)
        val panelArticle = createTestArticle("w", "Panel Text", 1)
        val tabularArticleForPrint = createTestArticle("p", "Tabular Text", 1)
        val articles = Array(tabularArticleForPrint, panelArticle, tabularArticleForWeb)

        assert(ArticleFinder.findBodyText(createTestBundle(articles)).contains(panelArticle))
      }
      "prefer 'object_type' of Tabular if there is no other option" in {
        val articleForPrint = createTestArticle("p", "Tabular Text", 1)
        val articleForWeb = createTestArticle("w", "Tabular Text", 1)
        val articles = Array(articleForPrint, articleForWeb)

        assert(ArticleFinder.findBodyText(createTestBundle(articles)).contains(articleForWeb))
      }
      "select the only option if only one available" in {
        val articleForPrint = createTestArticle("p", "Tabular Text", 1)
        val articles = Array(articleForPrint)

        assert(ArticleFinder.findBodyText(createTestBundle(articles)).contains(articleForPrint))
      }
      "select the option with the lowest object number" in {
        val articleForPrint = createTestArticle("p", "Tabular Text", 1)
        val articleForPrintSecond = createTestArticle("p", "Tabular Text", 2)
        val articles = Array(articleForPrintSecond, articleForPrint)

        assert(ArticleFinder.findBodyText(createTestBundle(articles)).contains(articleForPrint))
      }
    }
  }

}
