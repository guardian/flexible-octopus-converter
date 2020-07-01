package com.gu.octopusthrift.aws

import com.gu.octopusthrift.Config

trait DeadLetterQueue {
  val deadLetterQueue = new SQS(Config.apply)
}
