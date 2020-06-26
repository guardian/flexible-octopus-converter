package com.gu.octopusthrift.aws

import java.nio.ByteBuffer
import java.util.UUID

import com.amazonaws.services.kinesis.model.PutRecordRequest
import com.amazonaws.services.kinesis.{ AmazonKinesis, AmazonKinesisClientBuilder }
import com.gu.octopusthrift.Config
import com.gu.octopusthrift.services.Logging

class Kinesis(config: Config) extends Logging {

  private val builder = AmazonKinesisClientBuilder.defaultClient()

  private lazy val kinesisClient: AmazonKinesis = builder

  def publish(message: Array[Byte]): Unit = {
    val partitionKey = UUID.randomUUID().toString

    logger.info("Publishing message to kinesis")

    val data = ByteBuffer.wrap(message)

    val request = new PutRecordRequest()
      .withStreamName(config.thriftStreamName)
      .withPartitionKey(partitionKey)
      .withData(data)

    try {
      val result = kinesisClient.putRecord(request)
      logger.info(s"Published kinesis message: $result")
    } catch {
      case e: Exception =>
        logger.error(s"kinesis putRecord exception message: ${e.getMessage}")
    }
  }
}
