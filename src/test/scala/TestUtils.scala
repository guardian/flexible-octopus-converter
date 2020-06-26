package scala

import play.api.libs.json.{ JsValue, Json }

import scala.io.Source

object TestUtils {

  def readJson(path: String): JsValue = {
    val resource = getClass.getResourceAsStream(path)
    Json.parse(Source.fromInputStream(resource).getLines().mkString)
  }

}
