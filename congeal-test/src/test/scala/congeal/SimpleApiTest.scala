package congeal

import congeal.sc._

import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/** JUnit tests for `congeal.simpleApi` macro. */
@RunWith(classOf[JUnit4])
class SimpleApiTest {

  @Test
  def simpleApiFailsOnClass() {
    compilingSourceFailsWithErrorMessage(
      """|object Test extends App {
         |  class Foo
         |  type FooApi = congeal.simpleApi[Foo]
         |}
      |""".stripMargin,
      """|source.scala:3: error: simpleApi[A] only works if A is a trait
         |  type FooApi = congeal.simpleApi[Foo]
         |                        ^
         |one error found
      |""".stripMargin)
  }

  // TODO: test  - A must not have non-private inner classes
  // TODO: test  - A must not have methods that return A
  // TODO: test  - A must not have methods that have parameters of type A

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
