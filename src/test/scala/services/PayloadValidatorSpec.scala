package services

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.Json
import scala.io.Source

import com.gu.octopusthrift.services.PayloadValidator
import com.gu.octopusthrift.models.OctopusBundle
import java.nio.ByteBuffer
import scala.TestUtils

class PayloadValidatorSpec extends AnyWordSpec with Matchers {

  "PayloadValidator" when {
    "isValidBundle called" should {
      "return false for valid Json but no composer ID" in {
        val exampleJson = TestUtils.readJson(s"/exampleWithNoComposerId.json").as[OctopusBundle]
        PayloadValidator.isValidBundle(exampleJson) shouldBe false
      }
      "return false for valid Json with composer ID but no text body" in {
        val exampleJson = TestUtils.readJson(s"/exampleWithoutBodyText.json").as[OctopusBundle]
        PayloadValidator.isValidBundle(exampleJson) shouldBe false
      }
      "return false for valid Json with no articles" in {
        val exampleJson = TestUtils.readJson(s"/exampleWithNoArticles.json").as[OctopusBundle]
        PayloadValidator.isValidBundle(exampleJson) shouldBe false
      }
      "return false for valid Json but without any complete articles" in {
        val exampleJson = TestUtils.readJson(s"/exampleWithMissingArticleFields.json").as[OctopusBundle]
        PayloadValidator.isValidBundle(exampleJson) shouldBe false
      }
      "return true for valid Json and valid composer ID" in {
        val exampleJson = TestUtils.readJson(s"/example.json").as[OctopusBundle]
        PayloadValidator.isValidBundle(exampleJson) shouldBe true
      }
    }
    "validateSinglePayload is called" should {
      "correctly identify a single bundle payload" in {
        val exampleJson = TestUtils.readJson(s"/exampleBundleObject.json")
        PayloadValidator.validateSinglePayload(Json.toBytes(exampleJson)).isDefined shouldBe true
      }
      "return None for any other payload" in {
        val exampleJson = TestUtils.readJson(s"/exampleBundleCache.json")
        PayloadValidator.validateSinglePayload(Json.toBytes(exampleJson)).isEmpty shouldBe true
      }
    }
    "validateCachePayload is called" should {
      "correctly identify a cache payload" in {
        val exampleJson = TestUtils.readJson(s"/exampleBundleCache.json")
        PayloadValidator.validateCachePayload(Json.toBytes(exampleJson)).isDefined shouldBe true
      }
      "return None for any other payload" in {
        val exampleJson = TestUtils.readJson(s"/exampleBundleObject.json")
        PayloadValidator.validateCachePayload(Json.toBytes(exampleJson)).isEmpty shouldBe true
      }
    }
  }
}
