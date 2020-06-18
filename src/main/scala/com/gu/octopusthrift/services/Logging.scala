package com.gu.octopusthrift.services

import org.apache.logging.log4j.{ LogManager, Logger }

trait Logging {
  protected val logger: Logger = LogManager.getLogger(getClass)
}