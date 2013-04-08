package congeal

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

/** Contains top-level helper methods for tests that run scalac. */
package object sc {

  // TODO: compilingFile
  // TODO: compilingResource

  /** The result of compiling the provided Scala source. */
  def compilingSource(source: String): Compiling = compiling(source)

  /** Asserts that compiling the Scala source fails with the expected error message. */
  def compilingSourceFailsWithErrorMessage(source: String, expectedErrorMessage: String) {
    compilingSource(source).failsWithErrorMessage(expectedErrorMessage)
  }

  /** Asserts that compiling the Scala source succeeds, produces a runnable scala.App,
    * and that running the App produces the expected output.
    */
  def compilingSourceProducesAppWithOutput(source: String, appName: String, expectedOutput: String) {
    compilingSource(source).producesAppWithOutput(appName, expectedOutput)
  }

  /** The result of compiling the Scala source.
    * @param source the Scala source to compile
    */
  class Compiling(private val source: String) {
    lazy val compilationResult = new CompilationResult(source)

    /** Asserts that compiling the Scala source fails with the expected error message. */
    def failsWithErrorMessage(expectedErrorMessage: String) = {
      assertTrue(
        "compiling returns non-zero exit status",
        compilationResult.exitValue != 0)
      assertEquals(
        "compiling fails with error message",
        expectedErrorMessage,
        compilationResult.errorMessage.get)
      compilationResult.cleanup()
    }

    /** Asserts that compiling the Scala source succeeds, produces a runnable scala.App,
      * and that running the App produces the expected output.
      */
    def producesAppWithOutput(appName: String, expectedOutput: String) {
      assertEquals("compiling returns zero exit status", 0, compilationResult.exitValue)
      // FIX: extract AppExecutionResult
      val compilePath = compilationResult.compileDir.getPath
      val command = List(
        "bin/ivy-scala",
        "-cp", compilePath + ":congeal-main/target/scala-2.11/classes",
        appName)
      val processExecution = new ProcessExecution(command)
      assertEquals("running " + appName + " returns zero exit status", 0, processExecution.exit)
      assertEquals(
        "running " + appName + " produces expected output",
        expectedOutput,
        processExecution.output)
      compilationResult.cleanup()
    }
  }

  private def compiling(source: String): Compiling = new Compiling(source)
}
