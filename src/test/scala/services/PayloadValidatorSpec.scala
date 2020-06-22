package scala.services

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.Json
import scala.io.Source

import com.gu.octopusthrift.services.PayloadValidator
import java.nio.ByteBuffer
import scala.TestUtils

class PayloadValidatorSpec extends AnyWordSpec with Matchers {

  "PayloadValidator" when {
    "isValidPayload called" should {
      "return false for invalid JSON" in {
        val invalidData: Array[Byte] = Array(0).map(_.toByte)
        PayloadValidator.isValidPayload(invalidData) shouldBe false
      }
      "return false for valid Json but no composer ID" in {
        val exampleJson = TestUtils.readJson(s"/exampleWithNoComposerId.json")
        val validData = exampleJson.toString.map(_.toByte).toArray
        PayloadValidator.isValidPayload(validData) shouldBe false
      }
      "return false for valid Json with composer ID but no text body" in {
        val exampleJson = TestUtils.readJson(s"/exampleWithoutBodyText.json")
        val validData = exampleJson.toString.map(_.toByte).toArray
        PayloadValidator.isValidPayload(validData) shouldBe false
      }
      "return true for valid Json and valid composer ID" in {
        val exampleJson = TestUtils.readJson(s"/example.json")
        val validData = exampleJson.toString.map(_.toByte).toArray
        PayloadValidator.isValidPayload(validData) shouldBe true
      }
    }
  }

}