package congeal

import scala.tools.nsc.reporters.ConsoleReporter
import scala.tools.nsc.Global
import scala.tools.nsc.Settings

import java.io.File
import java.io.PrintWriter

// FIX doc throughout
package object sc {

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
      compileDir.delete()
    }
  }

  private def createCompileDir(): File = {
    val file = File.createTempFile("congeal.sc.", ".compile.dir")
    file.delete()
    file.mkdir()
    file
  }

  private def createSourceFile(source: String, compileDir: File): File = {
    val file = File.createTempFile("congeal.sc.", ".scala", compileDir)
    val writer = new PrintWriter(file)
    writer.print(source)
    writer.close()
    file
  }

  private def compileSourceFile(sourceFile: File): Boolean = {
    println(s"compileSourceFile $sourceFile")
    true
  }
}
