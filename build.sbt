import sbtassembly.AssemblyPlugin.autoImport._

ThisBuild / version := "1.0"
ThisBuild / scalaVersion := "2.13.10"

Test / fork := true
testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework"))

lazy val commonSettings = Seq(
  assembly / test := {},
  assembly / assemblyJarName := artifact.value.name + "-" + version.value + ".jar",
  assembly / assemblyOption := (assembly / assemblyOption).value
    .copy(includeScala = true, includeDependency = true),
  assembly / assemblyMergeStrategy := {
    case x if (x == "application.conf") || x.endsWith(".xml") => MergeStrategy.discard
    case PathList("META-INF", "MANIFEST.MF")                  => MergeStrategy.discard
    case PathList("META-INF", _*)                             => MergeStrategy.last
    case _                                                    => MergeStrategy.first
  },
  dependencyOverrides ++= circeDependencies ++ Seq(
    "io.netty"                   % "netty-transport-native-kqueue" % "4.1.87.Final",
    "io.netty"                   % "netty-transport-native-epoll"  % "4.1.87.Final",
    "com.fasterxml.jackson.core" % "jackson-databind"              % "2.13.5"
  ),
  javacOptions ++= Seq("-source", "1.8"),
  compileOrder := CompileOrder.JavaThenScala
)

lazy val circeVersion       = "0.14.1"
lazy val tapirVersion       = "1.2.5"
lazy val tapirClientVersion = "3.2.3"
lazy val zioConfigVersion   = "3.0.7"

lazy val driverDependencies = Seq(
  "ch.qos.logback"       % "logback-classic"          % "1.3.10",
  "net.logstash.logback" % "logstash-logback-encoder" % "7.2",
  "dev.zio"              %% "zio-logging-slf4j"       % "2.1.13"
)

lazy val configDependencies = Seq(
  "dev.zio" %% "zio-config",
  "dev.zio" %% "zio-config-typesafe",
  "dev.zio" %% "zio-config-magnolia"
).map(_ % zioConfigVersion)

lazy val tapirClientDependecies = Seq(
  "com.softwaremill.sttp.client3" %% "core" % tapirClientVersion,
  "org.jsoup"                     % "jsoup" % "1.14.3"
)

lazy val tapirDependencies = Seq(
  "com.softwaremill.sttp.tapir" %% "tapir-core",
  "com.softwaremill.sttp.tapir" %% "tapir-zio",
  "com.softwaremill.sttp.tapir" %% "tapir-zio-http-server",
  "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs",
  "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle",
  "com.softwaremill.sttp.tapir" %% "tapir-json-circe"
).map(_ % tapirVersion)

lazy val zioMetricsDependencies = Seq(
  "dev.zio" %% "zio-metrics-connectors"            % "2.2.0",
  "dev.zio" %% "zio-metrics-connectors-prometheus" % "2.2.0"
)

lazy val circeDependencies = Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

lazy val root = (project in file("."))
  .settings(
    name := "TwoGisCrawler",
    libraryDependencies ++= Seq(
      circeDependencies,
      driverDependencies,
      tapirDependencies,
      tapirClientDependecies,
      configDependencies,
      zioMetricsDependencies
    ).reduce(_ ++ _),
    commonSettings
  )
