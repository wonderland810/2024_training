logLevel := Level.Warn

resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.9.3")
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.15.0")
// for autoplugins
addSbtPlugin("com.github.sbt" % "sbt-native-packager" % "1.9.12")

