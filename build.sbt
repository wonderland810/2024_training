name := "delaySquareAPI"

version := "1.0"

lazy val `delaysquareapi` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "Akka Snapshot Repository" at "https://repo.akka.io/snapshots/"

scalaVersion := "2.11.12"

scalaVersion := "2.11.12"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play" % "2.5.18"
)
dependencyOverrides += "com.google.inject" % "guice" % "4.0"
dependencyOverrides += "com.typesafe.akka" %% "akka-slf4j" % "2.4.20"
dependencyOverrides += "io.netty" % "netty-transport" % "4.0.51.Final"
dependencyOverrides += "io.netty" % "netty-buffer" % "4.0.51.Final"
dependencyOverrides += "io.netty" % "netty-common" % "4.0.51.Final"


