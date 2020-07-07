package services

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

import com.gu.octopusthrift.services.ArticleFinder
import com.gu.octopusthrift.models._

class ArticleFinderSpec extends AnyWordSpec with Matchers {

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
      "retrieve the primary body text when there is more than one possible body text" in {
        val articleWithMultipleBodyTexts =
          TestUtils.readJson(s"/exampleWithMultipleBodyTexts.json").as[OctopusBundle]
        val articleWithBodyPanelAndTabularTexts =
          TestUtils.readJson(s"/exampleWithMultipleBodyPanelAndTabular.json").as[OctopusBundle]
        val expectedArticle = TestUtils.readJson(s"/article.json").as[OctopusArticle]

        assert(ArticleFinder.findBodyText(articleWithMultipleBodyTexts).contains(expectedArticle))
        assert(ArticleFinder.findBodyText(articleWithBodyPanelAndTabularTexts).contains(expectedArticle))
      }
      "prefer both to web and print" in {
        val articleForWeb = OctopusArticle(
          1233,
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
          Some("1"))
        val articleForPrint = OctopusArticle(
          1234,
          "article.t0",
          "p",
          "n",
          "Body Text",
          1,
          "202006241200",
          None,
          "N",
          "Writers",
          Some(1000),
          Some("1"))
        val articleForBoth = OctopusArticle(
          1233,
          "article.t0",
          "b",
          "n",
          "Body Text",
          1,
          "202006241200",
          None,
          "N",
          "Writers",
          Some(1000),
          Some("1"))

        val articles = Array(articleForPrint, articleForWeb, articleForBoth)

        val bundle = OctopusBundle(101, "", "", Some(""), "", articles)

        assert(ArticleFinder.findBodyText(bundle).contains(articleForBoth))
      }
      "prefer web to print" in {
        val articleForWeb = OctopusArticle(
          1233,
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
          Some("1"))
        val articleForPrint = OctopusArticle(
          1234,
          "article.t0",
          "p",
          "n",
          "Body Text",
          1,
          "202006241200",
          None,
          "N",
          "Writers",
          Some(1000),
          Some("1"))

        val articles = Array(articleForPrint, articleForWeb)

        val bundle = OctopusBundle(101, "", "", Some(""), "", articles)

        assert(ArticleFinder.findBodyText(bundle).contains(articleForWeb))
      }
      "prefer Body Text to other types" in {
        val articleObjectOne = OctopusArticle(
          1233,
          "article.t0",
          "w",
          "n",
          "Tabular Text",
          1,
          "202006241200",
          None,
          "N",
          "Writers",
          Some(1000),
          Some("1"))
        val articleObjectTwo = OctopusArticle(
          1234,
          "article.t0",
          "w",
          "n",
          "Body Text [Ruled]",
          2,
          "202006241200",
          None,
          "N",
          "Writers",
          Some(1000),
          Some("1"))
        val articleObjectThree = OctopusArticle(
          1234,
          "article.t0",
          "w",
          "n",
          "Panel Text",
          1,
          "202006241200",
          None,
          "N",
          "Writers",
          Some(1000),
          Some("1"))
        val articleObjectFour = OctopusArticle(
          1233,
          "article.t0",
          "w",
          "n",
          "Headline",
          1,
          "202006241200",
          None,
          "N",
          "Writers",
          Some(1000),
          Some("1"))

        val articles = Array(articleObjectFour, articleObjectTwo, articleObjectThree, articleObjectOne)

        val bundle = OctopusBundle(101, "", "", Some(""), "", articles)

        assert(ArticleFinder.findBodyText(bundle).contains(articleObjectTwo.copy(object_type = "Body Text")))
      }
      "prefer Panel Text to Tabular types" in {
        val tabularArticleForWeb = OctopusArticle(
          1233,
          "article.t0",
          "w",
          "n",
          "Tabular Text",
          1,
          "202006241200",
          None,
          "N",
          "Writers",
          Some(1000),
          Some("1"))
        val panelArticle = OctopusArticle(
          1234,
          "article.t0",
          "w",
          "n",
          "Panel Text",
          2,
          "202006241200",
          None,
          "N",
          "Writers",
          Some(1000),
          Some("1"))
        val tabularArticleForPrint = OctopusArticle(
          1234,
          "article.t0",
          "p",
          "n",
          "Tabular Text",
          1,
          "202006241200",
          None,
          "N",
          "Writers",
          Some(1000),
          Some("1"))

        val articles = Array(tabularArticleForPrint, panelArticle, tabularArticleForWeb)

        val bundle = OctopusBundle(101, "", "", Some(""), "", articles)

        assert(ArticleFinder.findBodyText(bundle).contains(panelArticle))
      }
      "prefer Tabular if there is no other option" in {
        val articleForPrint = OctopusArticle(
          1233,
          "article.t0",
          "p",
          "n",
          "Tabular Text",
          1,
          "202006241200",
          None,
          "N",
          "Writers",
          Some(1000),
          Some("1"))
        val articleForWeb = OctopusArticle(
          1234,
          "article.t0",
          "w",
          "n",
          "Tabular Text",
          2,
          "202006241200",
          None,
          "N",
          "Writers",
          Some(1000),
          Some("1"))

        val articles = Array(articleForPrint, articleForWeb)

        val bundle = OctopusBundle(101, "", "", Some(""), "", articles)

        assert(ArticleFinder.findBodyText(bundle).contains(articleForWeb))
      }
      "select the only option if only one available" in {
        val articleForPrint = OctopusArticle(
          1233,
          "article.t0",
          "p",
          "n",
          "Tabular Text",
          1,
          "202006241200",
          None,
          "N",
          "Writers",
          Some(1000),
          Some("1"))

        val articles = Array(articleForPrint)

        val bundle = OctopusBundle(101, "", "", Some(""), "", articles)

        assert(ArticleFinder.findBodyText(bundle).contains(articleForPrint))
      }
      "select the option with the lowest object number" in {
        val articleForPrint = OctopusArticle(
          1233,
          "article.t0",
          "p",
          "n",
          "Tabular Text",
          1,
          "202006241200",
          None,
          "N",
          "Writers",
          Some(1000),
          Some("1"))

        val articleForPrintSecond = OctopusArticle(
          1233,
          "article.t0",
          "p",
          "n",
          "Tabular Text",
          2,
          "202006241200",
          None,
          "N",
          "Writers",
          Some(1000),
          Some("1"))

        val articles = Array(articleForPrintSecond, articleForPrint)

        val bundle = OctopusBundle(101, "", "", Some(""), "", articles)

        assert(ArticleFinder.findBodyText(bundle).contains(articleForPrint))
      }
    }
  }

}
