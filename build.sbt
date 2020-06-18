name := "octopus-converter"

organization := "com.gu"

description:= "AWS Lambda providing conversion between Octopus JSON and Thrift"

version := "1.0"

scalaVersion := "2.13.1"

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-target:jvm-1.8",
  "-Ywarn-dead-code"
)

libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-lambda-java-core" % "1.2.1",
  "com.amazonaws" % "aws-lambda-java-log4j2" % "1.1.0",
  "com.amazonaws" % "aws-java-sdk-kinesis" % "1.11.804",
  "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.8.2",
  "org.slf4j" % "slf4j-api" % "1.7.30"
)

libraryDependencies += "org.scalactic" %% "scalactic" % "3.1.2"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.1.2" % "test"

libraryDependencies += "com.typesafe.play" %% "play-json" % "2.8.1" 

enablePlugins(RiffRaffArtifact)

assemblyJarName := s"${name.value}.jar"
riffRaffPackageType := assembly.value
riffRaffUploadArtifactBucket := Option("riffraff-artifact")
riffRaffUploadManifestBucket := Option("riffraff-builds")
riffRaffManifestProjectName := "Editorial Tools::Octopus Conversion Lambda"

assemblyMergeStrategy in assembly := {
      case PathList("META-INF", xs@_*) => MergeStrategy.discard
      case _ => MergeStrategy.first
}