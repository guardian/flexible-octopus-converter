package scala.models

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

import com.gu.octopusthrift.models.{ OctopusArticle, OctopusBundle }
import com.gu.flexibleoctopus.model.thrift._

object BundleTestHelpers {
  def createOctopusBundleWithArticle(article: OctopusArticle) =
    OctopusBundle(2345, ",,,,,,,1", "", Some("20200624"), "", Array(article))
}

class OctopusBundleSpec extends AnyWordSpec with Matchers {
  "OctopusBundle" when {
    "as[StoryBundle] is called" should {
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
          Some("1"))

        val octopusBundle = OctopusBundle(2345, ",,,,,,,1", "", Some("20200624"), "", Array(octopusArticle))

        val thriftBundle = octopusBundle.as[StoryBundle]

        assert(thriftBundle.bodyText.id == 1234)
        assert(thriftBundle.composerId == "1")
        assert(thriftBundle.pageNumber == Some(1))
        assert(thriftBundle.printPublicationDate == Some(18437))
        assert(thriftBundle.octopusLayoutId == Some("1000"))
      }
      "handle no info8 data" in {
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
          Some("1"))

        val octopusBundle = OctopusBundle(2345, "", "", Some("20200624"), "", Array(octopusArticle))

        val thriftBundle = octopusBundle.as[StoryBundle]

        assert(thriftBundle.composerId == "")
      }
      "return pageNumber None when not present" in {
        val article = ArticleTestHelpers.createOctopusArticleWithPage(None)
        val bundle = BundleTestHelpers.createOctopusBundleWithArticle(article)
        val thriftBundle = bundle.as[StoryBundle]

        thriftBundle.pageNumber shouldBe None
      }
      "return pageNumber when present" in {
        val article = ArticleTestHelpers.createOctopusArticleWithPage(Some("34"))
        val bundle = BundleTestHelpers.createOctopusBundleWithArticle(article)
        val thriftBundle = bundle.as[StoryBundle]

        thriftBundle.pageNumber shouldBe Some(34)
      }
      "return first pageNumber when in mixed list" in {
        val article = ArticleTestHelpers.createOctopusArticleWithPage(Some("25,26,27;31"))
        val bundle = BundleTestHelpers.createOctopusBundleWithArticle(article)
        val thriftBundle = bundle.as[StoryBundle]

        thriftBundle.pageNumber shouldBe Some(25)
      }
      "return first pageNumber when in comma separated list" in {
        val article = ArticleTestHelpers.createOctopusArticleWithPage(Some("1,2,3,4"))
        val bundle = BundleTestHelpers.createOctopusBundleWithArticle(article)
        val thriftBundle = bundle.as[StoryBundle]

        thriftBundle.pageNumber shouldBe Some(1)
      }
      "return first pageNumber when in semi colon separated list" in {
        val article = ArticleTestHelpers.createOctopusArticleWithPage(Some("10;11"))
        val bundle = BundleTestHelpers.createOctopusBundleWithArticle(article)
        val thriftBundle = bundle.as[StoryBundle]

        thriftBundle.pageNumber shouldBe Some(10)
      }
      "return None when page string is empty" in {
        val article = ArticleTestHelpers.createOctopusArticleWithPage(Some(""))
        val bundle = BundleTestHelpers.createOctopusBundleWithArticle(article)
        val thriftBundle = bundle.as[StoryBundle]

        thriftBundle.pageNumber shouldBe None
      }
      "return None when page string is a non-long" in {
        val article = ArticleTestHelpers.createOctopusArticleWithPage(Some("one"))
        val bundle = BundleTestHelpers.createOctopusBundleWithArticle(article)
        val thriftBundle = bundle.as[StoryBundle]

        thriftBundle.pageNumber shouldBe None
      }
    }
  }
}
