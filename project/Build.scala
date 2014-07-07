import sbt._
import sbt.Keys._

object TwitterBotBuild extends Build {
  val repositories = Seq(
    "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/",
    "Typesafe Snapshot Repo" at "http://repo.typesafe.com/typesafe/snapshots/"
  )

  val dependencies = Seq(
    "org.scribe" % "scribe" % "1.3.2",
	"org.mockito" % "mockito-all" % "1.9.5",
	"org.scala-tools.testing" % "scalacheck_2.9.1" % "1.9",
	"org.specs2" % "specs2_2.9.1" % "1.8" % "test",
    "com.typesafe.play" % "play_2.10" % "2.2.2"
  )
  
  lazy val twitterBot = Project(
    id = "twitter-bot",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      resolvers ++= repositories,
      name := "Twitter Bot",
      organization := "randyumi",
      version := "0.1-SNAPSHOT",
      scalaVersion := "2.10.2",
      // add other settings here
	  libraryDependencies ++= dependencies
    )
  )
}
