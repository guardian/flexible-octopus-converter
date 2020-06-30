package com.gu.octopusthrift.aws

import com.gu.octopusthrift.Config

trait CustomMetrics {
  val cloudWatch: CloudWatch = new CloudWatch(Config.apply)
}
