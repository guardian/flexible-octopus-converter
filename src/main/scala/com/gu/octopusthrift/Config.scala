package com.gu.octopusthrift

case class Config(thriftStreamName: String)

object Config {
  def apply: Config = {
    // TODO: add validation and fail fast if we don't have all config props
    Config(thriftStreamName = System.getenv("thriftStream"))
  }
}