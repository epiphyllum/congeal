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
      "/Users/sullivan/.ivy2/cache/org.scala-lang.macro-paradise/scala-reflect/jars/compiler-reflect-2.11.0-SNAPSHOT.jar",
      "/Users/sullivan/.ivy2/cache/org.scala-lang.macro-paradise/scala-reflect/jars/library-reflect-2.11.0-SNAPSHOT.jar",
      "/Users/sullivan/.ivy2/cache/org.scala-lang.macro-paradise/scala-reflect/jars/scala-reflect-2.11.0-SNAPSHOT.jar"
    ) foreach { path =>
      settings.classpath.append(path)
      settings.bootclasspath.append(path)
      //settings.toolcp.append(path)
    }
  }
}
