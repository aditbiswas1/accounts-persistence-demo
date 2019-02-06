name := "akka-quickstart-scala"

version := "1.0"

scalaVersion := "2.12.6"

lazy val akkaVersion = "2.5.20"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,

  // akka persistance dependencies
  "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
  "org.iq80.leveldb" % "leveldb" % "0.7",

  // akka http dependencies
  "com.typesafe.akka" %% "akka-http" % "10.1.7",
  "com.typesafe.akka" %% "akka-stream" % "2.5.20",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.7",
  
  "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8",

  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)
