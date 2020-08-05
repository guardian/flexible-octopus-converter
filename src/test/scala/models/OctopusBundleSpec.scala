package models

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import com.gu.octopusthrift.models.{ OctopusArticle, OctopusBundle }
import com.gu.flexibleoctopus.model.thrift._

import scala.util.Try

object BundleTestHelpers {
  def createOctopusBundleWithArticle(article: OctopusArticle) =
    OctopusBundle(2345, ",,,,,,,1", "", Some("20200624"), "", Array(article))
}

class OctopusBundleSpec extends AnyWordSpec with Matchers {
  "OctopusBundle" when {
    "as[StoryBundle] is called" should {
      "map the values correctly" in {

        def createArticle(forPublication: String) =
          OctopusArticle(
            1234,
            "article.t0",
            forPublication,
            "n",
            "Body Text",
            1,
            "202006241200",
            None,
            "N",
            "Writers",
            Some(1000),
            Some("1"),
            "Test User"
          )

        def createBundle(article: OctopusArticle) =
          OctopusBundle(2345, ",,,,,,,1", "", Some("20200624"), "", Array(article))

        val octopusArticleForWeb = createArticle("w")
        val octopusArticleForPrint = createArticle("p")
        val octopusArticleForBoth = createArticle("b")

        val octopusBundleForWeb = createBundle(octopusArticleForWeb)
        val octopusBundleForPrint = createBundle(octopusArticleForPrint)
        val octopusBundleForBoth = createBundle(octopusArticleForBoth)

        val thriftBundleForWeb = octopusBundleForWeb.as[StoryBundle]
        val thriftBundleForPrint = Try(octopusBundleForPrint.as[StoryBundle])
        val thriftBundleForBoth = Try(octopusBundleForBoth.as[StoryBundle])

        assert(thriftBundleForPrint.isSuccess)
        assert(thriftBundleForBoth.isSuccess)
        assert(thriftBundleForWeb.bodyText.id == 1234)
        assert(thriftBundleForWeb.composerId == "1")
        assert(thriftBundleForWeb.pageNumber == Some(1))
        assert(thriftBundleForWeb.printPublicationDate == Some(18437))
        assert(thriftBundleForWeb.octopusLayoutId == Some("1000"))
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
          Some("1"),
          "Test User"
        )

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
      "handle empty optional values correctly when parsing into a StoryBundle" in {
        val article = OctopusArticle(
          15313009,
          "Mesple.t01",
          "B",
          "P",
          "Body Text",
          1,
          "202006291255",
          Some(""), // in use by
          "N",
          "Desk",
          Some(0),
          Some(""), // on pages
          "Test User"
        )
        val bundle = OctopusBundle(
          15313010,
          "Mesple,483,,8,0,20200629125607,,5ef9bfff8f08aff12bef6572,20200629125602,0,Music,1",
          "gdn",
          Some(""), // pub date
          "1jo",
          Array(article)
        )

        val storyBundle = bundle.as[StoryBundle]

        storyBundle.composerId shouldBe "5ef9bfff8f08aff12bef6572"

      }
    }
  }
  "composerId is called" should {
    "correctly extract the Composer ID when present" in {
      val bundle = TestUtils.readJson(s"/example.json").as[OctopusBundle]
      bundle.composerId shouldBe Some("5ecd2cc68f087412dad10d1d")
    }
    "return a None when there's no Composer ID" in {
      val bundle = TestUtils.readJson(s"/exampleWithNoComposerId.json").as[OctopusBundle]
      bundle.composerId shouldBe None
    }
  }
}
