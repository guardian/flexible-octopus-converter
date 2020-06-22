package com.gu.octopusthrift.services

import play.api.libs.json._
import scala.util.{ Try, Success }
import com.gu.octopusthrift.services.ArticleFinder
import com.gu.octopusthrift.services.Logging

object PayloadValidator extends Logging {

  def isValidPayload(data: Array[Byte]): Boolean = {
    Try(Json.parse(data)) match {
      case Success(json) => {
        //check composer ID and body text is present
        hasComposerId(json) && ArticleFinder.findBodyText(json).isDefined
      }
      case _ => false
    }
  }

  private def hasComposerId(json: JsValue): Boolean = {
    Try((json \ "info8").get.as[JsString].value.split(',')(7)) match {
      case Success(composerId) => composerId.trim.length > 0
      case _ => {
        logger.info("composer ID not found")
        false
      }
    }
  }

}