package congeal

import scala.tools.nsc.reporters.ConsoleReporter
import scala.tools.nsc.Global
import scala.tools.nsc.Settings

import java.io.File
import java.io.PrintWriter

// FIX doc throughout
package object sc {

  lazy val settings = new Settings
  init()

  // TODO: compilingFile
  // TODO: compilingResource

  def compilingSource(source: String): Boolean = compiling(source)

  def compiling(source: String): Boolean = {
    val compileDir = createCompileDir()
    val sourceFile = createSourceFile(source, compileDir)
    try {
      compileSourceFile(sourceFile)
    }
    finally {
      sourceFile.delete()
    }
  }

  private def createCompileDir(): File = {
    val file = File.createTempFile("congeal.sc", "compile.dir")
    file.delete()
    file.mkdir()
    file
  }

  private def createSourceFile(source: String, compileDir: File): File = {
    val file = File.createTempFile("congeal.sc", "scala", compileDir)
    val writer = new PrintWriter(file)
    writer.print(source)
    writer.close()
    file
  }

  private def compileSourceFile(sourceFile: File): Boolean = {
    println("compile")
    val reporter = new ConsoleReporter(settings)
    val compiler = new Global(settings, reporter)

    val run = new compiler.Run
    run compile List(sourceFile.getPath)

    true

    //Scalac.process(Array(sourceFile.getPath))
    //Scalac.reporter.hasErrors
  }

  private def init() {
    println("init")
    Seq(
      "/home/johnny/ws/congeal/congeal-main/target/scala-2.11/congeal-main_2.11-0.0.0.jar",
      "/home/johnny/.ivy2/cache/org.scala-lang.macro-paradise/scala-compiler/jars/scala-compiler-2.11.0-SNAPSHOT.jar",
      "/home/johnny/.ivy2/cache/org.scala-lang.macro-paradise/scala-actors/jars/scala-actors-2.11.0-SNAPSHOT.jar",
      "/home/johnny/.ivy2/cache/org.scala-lang.macro-paradise/scala-partest/jars/scala-partest-2.11.0-SNAPSHOT.jar",
      "/home/johnny/.ivy2/cache/org.scala-lang.macro-paradise/scala-language/jars/scala-language-2.11.0-SNAPSHOT.jar",
      "/home/johnny/.ivy2/cache/org.scala-lang.macro-paradise/scala-reflect/jars/scala-reflect-2.11.0-SNAPSHOT.jar"
      // "/Users/sullivan/.ivy2/cache/org.scala-lang.macro-paradise/scala-reflect/jars/scala-reflect-2.11.0-SNAPSHOT.jar"
    ) foreach { path =>
      settings.classpath.append(path)
      settings.bootclasspath.append(path)
    }
  }
}
