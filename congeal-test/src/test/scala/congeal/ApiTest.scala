package congeal

import congeal.sc._

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/** JUnit tests for `congeal.api` macro. */
@RunWith(classOf[JUnit4])
class ApiTest {

  // TODO: remove this once i have some genuine failure cases
  @Test
  def basicCompilerError() {
    compilingSourceFailsWithErrorMessage(
      """ss
      |import congeal.api
      |import congeal.C
      |object Test extends App {
      |  class Foo { def bar = println("hi from foo") }
      |  val goo = new api[Foo]
      |  goo.bar
      |}
      |""".stripMargin,
      """source.scala:1: error: expected class or object definition
      |ss
      |^
      |one error found
      |""".stripMargin)
  }

  // FIX: should produce "hi from foo\n"!
  @Test
  def congealApiSaysHello() {
    compilingSourceProducesAppWithOutput(
      """
      |import congeal.api
      |import congeal.C
      |object Test extends App {
      |  class Foo { def bar = println("hi from foo") }
      |  val goo = new api[Foo]
      |  goo.bar
      |}
      |""".stripMargin,
      "Test",
      "hello world\n")
  }
  
}
