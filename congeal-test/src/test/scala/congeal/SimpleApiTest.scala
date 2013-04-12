package congeal

import congeal.sc._

import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/** JUnit tests for `congeal.simpleApi` macro. */
@RunWith(classOf[JUnit4])
class SimpleApiTest {

  // TODO: tests need to run in parallel

  // FIX: should produce "hi from foo\n"!
  @Test
  def simpleApiSaysHello() {
    compilingSourceProducesAppWithOutput(
      """|import congeal.simpleApi
         |object Test extends App {
         |  trait Foo { def bar = println("hi from foo") }
         |  val goo = new simpleApi[Foo]
         |  goo.bar
         |}
      |""".stripMargin,
      "Test",
      "hi from the other side\n")
  }

}
