package scala.services

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.Json
import scala.io.Source

import com.gu.octopusthrift.services.PayloadValidator
import com.gu.octopusthrift.models.OctopusBundleCache
import java.nio.ByteBuffer
import scala.TestUtils

class PayloadValidatorSpec extends AnyWordSpec with Matchers {

  "PayloadValidator" when {
    "isValidBundle called" should {
      "return false for valid Json but no composer ID" in {
        val exampleJson = TestUtils.readJson(s"/exampleWithNoComposerId.json")
        PayloadValidator.isValidBundle(exampleJson) shouldBe false
      }
      "return false for valid Json with composer ID but no text body" in {
        val exampleJson = TestUtils.readJson(s"/exampleWithoutBodyText.json")
        PayloadValidator.isValidBundle(exampleJson) shouldBe false
      }
      "return false for valid Json with no articles" in {
        val exampleJson = TestUtils.readJson(s"/exampleWithNoArticles.json")
        PayloadValidator.isValidBundle(exampleJson) shouldBe false
      }
      "return false for valid Json but without any complete articles" in {
        val exampleJson = TestUtils.readJson(s"/exampleWithMissingArticleFields.json")
        PayloadValidator.isValidBundle(exampleJson) shouldBe false
      }
      "return true for valid Json and valid composer ID" in {
        val exampleJson = TestUtils.readJson(s"/example.json")
        PayloadValidator.isValidBundle(exampleJson) shouldBe true
      }
    }
    "getBundleOrBundleCache" should {
      "return a single bundle when only one bundle is present" in {
        val exampleJson = TestUtils.readJson(s"/example.json")
        val actual = PayloadValidator.getBundleOrBundleCache(exampleJson)
        actual.isLeft shouldBe true
      }
      "return a bundle cache when there are multiple bundles present" in {
        val exampleJson = TestUtils.readJson(s"/exampleBundleCache.json")
        val actual = PayloadValidator.getBundleOrBundleCache(exampleJson)
        actual.isRight shouldBe true
      }
    }

  }
}
