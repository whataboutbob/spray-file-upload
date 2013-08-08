import sbt._
import Keys._

object BuildSettings {
  val buildSettings = Defaults.defaultSettings ++ Seq(
    organization := "spray.examples",
    name := "file-upload",
    version := "1.0.0",
    scalaVersion := "2.10.2"
  )
}

object Dependencies {
  /*NOTE:
    Spray 1.2-M8 is incompatible with Akka 2.2.0 final, either use
    Akka 2.2.0-RC1 or upgrade to a more recently nightly build of Spray 1.2.
   */
  val akka_actor = "com.typesafe.akka" % "akka-actor_2.10" % "2.2.0-RC1"
  val spray_json = "io.spray" %% "spray-json" % "1.2.5"
  val spray_can = "io.spray" % "spray-can" % "1.2-M8"
  val spray_httpx = "io.spray" % "spray-httpx" % "1.2-M8"
  val spray_routing = "io.spray" % "spray-routing" % "1.2-M8"
}

object Resolvers {
  val resolver = Seq("Spray Repo" at "http://repo.spray.io")
}

object MyBuild extends Build {
  import Resolvers._
  import Dependencies._
  import BuildSettings._

  // Sub-project specific dependencies
  val commonDeps = Seq(
    akka_actor,
    spray_json,
    spray_can,
    spray_httpx,
    spray_routing
  )

  lazy val root = Project(
    id = "flle-upload",
    base = file("."),
    settings = buildSettings ++ Seq(
      libraryDependencies ++= commonDeps,
      resolvers ++= resolver)
  )
}
