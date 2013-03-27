import sbt._
import Keys._

object BuildSettings {

  val buildSettings = Defaults.defaultSettings ++ Seq(
    organization := "mscs.sullivan.john",
    version := "0.0.0",
    scalacOptions ++= Seq(),
    scalaVersion := "2.11.0-SNAPSHOT",
    scalaOrganization := "org.scala-lang.macro-paradise",
    resolvers += Resolver.sonatypeRepo("snapshots")
  )
}

object MyBuild extends Build {
  import BuildSettings._

  lazy val parent = Project(
    "congeal-parent",
    file("."),
    settings = buildSettings
  ) aggregate(main, test)

  lazy val main = Project(
    "congeal-main",
    file("congeal-main"),
    settings = buildSettings ++ Seq(
      libraryDependencies <+= (scalaVersion)("org.scala-lang.macro-paradise" % "scala-reflect" % _))
  )

  lazy val test = Project(
    "congeal-test",
    file("congeal-test"),
    settings = buildSettings ++ Seq(
      libraryDependencies += "com.novocode" % "junit-interface" % "0.10-M3" % "test",
      libraryDependencies += "junit" % "junit" % "4.11" % "test")
  ) dependsOn(main)
}
