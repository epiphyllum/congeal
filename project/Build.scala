import sbt._
import Keys._

object BuildSettings {

  val buildSettings = Defaults.defaultSettings ++ Seq(
    organization := "mscs.sullivan.john",
    version := "0.4.0-SNAPSHOT",
    scalacOptions ++= Seq("-deprecation", "-feature"),
    scalaVersion := "2.11.0-SNAPSHOT",
    scalaOrganization := "org.scala-lang.macro-paradise",
    resolvers += Resolver.sonatypeRepo("snapshots")
  )
}

// FIX: DRY the libraryDependencies

object MyBuild extends Build {
  import BuildSettings._

  lazy val parent = Project(
    "congeal-parent",
    file("."),
    settings = buildSettings
  ) aggregate(main, examples, test)

  lazy val main = Project(
    "congeal-main",
    file("congeal-main"),
    settings = buildSettings ++ Seq(
      libraryDependencies <+= (scalaVersion)("org.scala-lang.macro-paradise" % "scala-reflect" % _),
      libraryDependencies += "org.easymock" % "easymock" % "3.1" % "optional",
      libraryDependencies += "org.jmock" % "jmock-junit4" % "2.6.0" % "optional")
  )

  lazy val examples = Project(
    "congeal-examples",
    file("congeal-examples"),
    settings = buildSettings ++ Seq(
      libraryDependencies <+= (scalaVersion)("org.scala-lang.macro-paradise" % "scala-reflect" % _),
      libraryDependencies += "org.easymock" % "easymock" % "3.1",
      libraryDependencies += "junit" % "junit" % "4.11")
  ) dependsOn(main)

  lazy val test = Project(
    "congeal-test",
    file("congeal-test"),
    settings = buildSettings ++ Seq(
      libraryDependencies += "com.novocode" % "junit-interface" % "0.10-M3",
      libraryDependencies += "org.easymock" % "easymock" % "3.1", // not used here yet
      libraryDependencies += "junit" % "junit" % "4.11")
  ) dependsOn(main, examples)
}
