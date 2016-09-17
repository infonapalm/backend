name := "RIDBackend"
organization := "com.infonapalm.ridbackend"
version := "2.1.2"
scalaVersion := "2.10.6"
parallelExecution in ThisBuild := false

Revolver.settings

lazy val versions = new {
  val finatra = "2.1.2"
  val guice = "4.0"
  val logback = "1.0.13"
}

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  "Twitter Maven" at "https://maven.twttr.com"
)

mainClass in assembly := Some("com.infonapalm.ridbackend.ServerMain")

assemblyMergeStrategy in assembly := {
  case "BUILD" => MergeStrategy.discard
  case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
  case other => MergeStrategy.first
}

unmanagedResourceDirectories in Compile += {
  baseDirectory.value / "src/main/webapp"
}

libraryDependencies ++= Seq(
  "com.twitter.finatra" %% "finatra-http" % versions.finatra,
  "com.twitter.finatra" %% "finatra-httpclient" % versions.finatra,
  "com.twitter.finatra" %% "finatra-slf4j" % versions.finatra,
  "com.twitter.inject" %% "inject-core" % versions.finatra,
  "ch.qos.logback" % "logback-classic" % versions.logback,

  "org.json4s" %% "json4s-jackson" % "3.2.10",
  "com.typesafe.slick" %% "slick" % "2.1.0",
  "org.squeryl" %% "squeryl" % "0.9.5-7",
  "com.h2database" % "h2" % "1.3.166",
  "c3p0" % "c3p0" % "0.9.1.2",
  "postgresql" % "postgresql" % "9.1-901.jdbc4",
  "mysql" % "mysql-connector-java" % "5.1.12",
  "com.typesafe.akka" %% "akka-actor" % "2.3.7",
  "com.typesafe.akka" %% "akka-testkit" % "2.3.7",
  "org.scalaj" %% "scalaj-http" % "2.2.1",
  "io.github.morgaroth" % "akka-rabbitmq_2.10" % "1.2.8",
  "com.ui4j" % "ui4j-all" % "2.1.0",

  "com.twitter.finatra" %% "finatra-http" % versions.finatra % "test",
  "com.twitter.finatra" %% "finatra-jackson" % versions.finatra % "test",
  "com.twitter.inject" %% "inject-server" % versions.finatra % "test",
  "com.twitter.inject" %% "inject-app" % versions.finatra % "test",
  "com.twitter.inject" %% "inject-core" % versions.finatra % "test",
  "com.twitter.inject" %% "inject-modules" % versions.finatra % "test",
  "com.google.inject.extensions" % "guice-testlib" % versions.guice % "test",

  "com.twitter.finatra" %% "finatra-http" % versions.finatra % "test" classifier "tests",
  "com.twitter.finatra" %% "finatra-jackson" % versions.finatra % "test" classifier "tests",
  "com.twitter.inject" %% "inject-app" % versions.finatra % "test" classifier "tests",
  "com.twitter.inject" %% "inject-core" % versions.finatra % "test" classifier "tests",
  "com.twitter.inject" %% "inject-modules" % versions.finatra % "test" classifier "tests",
  "com.twitter.inject" %% "inject-server" % versions.finatra % "test" classifier "tests",

  "org.mockito" % "mockito-core" % "1.9.5" % "test",
  "org.scalatest" %% "scalatest" % "2.2.3" % "test")
