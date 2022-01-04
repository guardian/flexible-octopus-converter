name := "octopus-converter"

organization := "com.gu"

description := "AWS Lambda providing conversion between Octopus JSON and Thrift"

version := "1.0"

scalaVersion := "2.13.7"

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-target:jvm-1.8",
  "-Ywarn-dead-code"
)
val awsSdkVersion = "1.11.804"

libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-lambda-java-core" % "1.2.1",
  "com.amazonaws" % "aws-lambda-java-log4j2" % "1.5.1",
  "com.amazonaws" % "aws-java-sdk-kinesis" % awsSdkVersion,
  "com.amazonaws" % "aws-java-sdk-sqs" % awsSdkVersion,
  "com.amazonaws" % "aws-java-sdk-cloudwatch" % awsSdkVersion,
  "com.amazonaws" % "aws-lambda-java-events" % "1.3.0",
  "org.slf4j" % "slf4j-simple" % "1.7.25",
  "org.scalactic" %% "scalactic" % "3.1.2",
  "org.scalatest" %% "scalatest" % "3.1.2" % "test",
  "com.typesafe.play" %% "play-json" % "2.8.1",
  "com.fasterxml.jackson.dataformat" % "jackson-dataformat-cbor" % "2.11.0",
  "com.gu" %% "flexible-octopus-model" % "0.5.0",
  "org.apache.thrift" % "libthrift" % "0.13.0",
  "com.twitter" %% "scrooge-core" % "20.5.0"
)

enablePlugins(RiffRaffArtifact)

assemblyJarName := s"${name.value}.jar"
riffRaffPackageType := assembly.value
riffRaffUploadArtifactBucket := Option("riffraff-artifact")
riffRaffUploadManifestBucket := Option("riffraff-builds")
riffRaffManifestProjectName := "Editorial Tools::Octopus Conversion Lambda"

assembly / assemblyMergeStrategy := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case _                             => MergeStrategy.first
}
