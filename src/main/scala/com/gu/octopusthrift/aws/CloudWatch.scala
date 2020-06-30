package com.gu.octopusthrift.aws

import com.amazonaws.services.cloudwatch.AmazonCloudWatch
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder
import com.amazonaws.services.cloudwatch.model.Dimension
import com.amazonaws.services.cloudwatch.model.MetricDatum
import com.amazonaws.services.cloudwatch.model.PutMetricDataRequest
import com.amazonaws.services.cloudwatch.model.PutMetricDataResult
import com.amazonaws.services.cloudwatch.model.StandardUnit
import com.gu.octopusthrift.Config
import com.gu.octopusthrift.services.Logging;

object Metrics {
  val MissingMandatoryBundleData = "MissingMandatoryBundleData"
  val FailedThriftConversion = "FailedThriftConversion"
  val MissingExpectedPayloadData = "MissingExpectedPayloadData"
}

class CloudWatch(config: Config) extends Logging {

  private val builder = AmazonCloudWatchClientBuilder.defaultClient()

  private lazy val cloudWatchClient = builder

  def publishMetricEvent(metric: String): Unit = {

    val dimension = new Dimension()
      .withName("Stage")
      .withValue(config.stage);

    val datum = new MetricDatum()
      .withMetricName(metric)
      .withUnit(StandardUnit.Count)
      .withValue(1)
      .withDimensions(dimension)

    val request = new PutMetricDataRequest()
      .withNamespace("OctopusLambda")
      .withMetricData(datum)

    try {
      val result = cloudWatchClient.putMetricData(request)
      logger.info(s"Published metric data: $result")
    } catch {
      case e: Exception =>
        logger.error(s"CloudWatch putMetricData exception message: ${e.getMessage}")
    }
  }
}
