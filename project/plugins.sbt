addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.5")

addSbtPlugin("com.gu" % "sbt-riffraff-artifact" % "1.1.9")

addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.8.0")

resolvers += Resolver.url(
  "idio",
  url("http://dl.bintray.com/idio/sbt-plugins")
)(Resolver.ivyStylePatterns)

addSbtPlugin("org.idio" % "sbt-assembly-log4j2" % "0.1.0")