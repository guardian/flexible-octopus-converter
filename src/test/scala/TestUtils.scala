package scala

import play.api.libs.json.Json
import scala.io.Source

object TestUtils {

  def readJson(path: String) = {
    val resource = getClass.getResourceAsStream(path)
    Json.parse(Source.fromInputStream(resource).getLines().mkString)
  }

}