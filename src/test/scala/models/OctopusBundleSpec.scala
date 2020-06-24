package scala.models

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.Json

import scala.io.Source
import com.gu.octopusthrift.models.{ OctopusArticle, OctopusBundle }
import com.gu.flexibleoctopus.model.thrift._

import scala.TestUtils

object BundleHelpers {
  def setupOctopusArticleWithPage(onPages: Option[String]) = OctopusArticle(
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
    onPages)
}

class OctopusBundleSpec extends AnyWordSpec {
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

        val octopusBundle = OctopusBundle(
          2345,
          ",,,,,,,1",
          "",
          Some("20200624"),
          "",
          Array(octopusArticle))

        val thriftBundle = octopusBundle.as[StoryBundle]

        assert(thriftBundle.bodyText.id == 1234)
        assert(thriftBundle.composerId == "1")
        assert(thriftBundle.pageNumber == Some(1))
        assert(thriftBundle.printPublicationDate == Some(18437))
        assert(thriftBundle.octopusLayoutId == Some("1000"))
      }
    }
  }
}
