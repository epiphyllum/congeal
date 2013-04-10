package congeal.sc

import java.io.File
import java.io.PrintWriter

/** Compiles Scala source code, capturing exit status, output, and error streams
  * from the Scala compiler. The source code is provided in a string representation.
  * This gets written into a file in a temporary directory. Class files are
  * generated to the same temporary directory. The version of scalac that is used
  * is called bin/ivy-scalac, which runs the 2.11.0-SNAPSHOT compiler directly out
  * of the user's Ivy cache.
  */
class CompilationResult(private val source: String) {

  /** The temporary directory where the compilation is run. */
  val compileDir = createCompileDir()

  private val sourceFile = createSourceFile(source, compileDir)
  private val (exit, output, error) = compileSourceFile(sourceFile, compileDir)

  /** The exit status of the compilation process. */
  def exitValue: Int = exit

  /** None if the compilation succeeded; otherwise a Some containing any output
    * sent by the compiler to the error stream.
    */
  def errorMessage: Option[String] = if (exit == 0) None else Some(error)

  /** Cleans up by removing the temporary compile directory and all its contents. */
  def cleanup() {
    // TODO: this will not be sufficient if user specifies non-default package
    compileDir.listFiles foreach { file => file.delete() }
    compileDir.delete()
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

  private def compileSourceFile(sourceFile: File, compileDir: File): (Int, String, String) = {
    val command = List(
      "bin/ivy-scalac",
      "-deprecation",
      "-cp", "congeal-main/target/scala-2.11/classes",
      "-d", compileDir.getPath,
      sourceFile.getPath)
    val processExecution = new ProcessExecution(command)

    (processExecution.exit,
     processExecution.output.replace(sourceFile.getPath, "source.scala"),
     processExecution.error.replace(sourceFile.getPath, "source.scala"))
  }
}
