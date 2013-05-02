package congeal.constraints

import congeal.sc._

import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/** JUnit tests to make sure the `congeal.api` macro constrains its type parameter as
  * expected by rejecting (emitting error on) type parameters that do not match constraints.
  */
@RunWith(classOf[JUnit4])
class ApiConstrainsTypeParameterTest {

  @Test
  def apiCompilesOnTrait() {
    compilingSourceSucceeds(
      """|object Test extends App {
         |  trait Foo
         |  type FooApi = congeal.api[Foo]
         |}
      |""".stripMargin)
  }

  @Test
  def apiDoesNotCompileOnClass() {
    compilingSourceErrorsWithMessage(
      """|object Test extends App {
         |  class Foo
         |  type FooApi = congeal.api[Foo]
         |}
      |""".stripMargin,
      """|source.scala:3: error: Foo must be a trait in api[Foo]
         |  type FooApi = congeal.api[Foo]
         |                        ^
         |one error found
      |""".stripMargin)
  }

  @Test
  def apiDoesNotCompileOnTraitInsideTrait() {
    compilingSourceErrorsWithMessage(
      """|trait A {
         |  trait Foo
         |  type FooApi = congeal.api[Foo]
         |}
      |""".stripMargin,
      """|source.scala:3: error: Foo must be static in api[Foo]
         |  type FooApi = congeal.api[Foo]
         |                        ^
         |one error found
      |""".stripMargin)
  }

  @Test
  def apiDoesNotCompileOnTraitInsideMethod() {
    compilingSourceErrorsWithMessage(
      """|trait T {
         |  def a {
         |    trait Foo
         |    type FooApi = congeal.api[Foo]
         |  }
         |}
      |""".stripMargin,
      """|source.scala:4: error: Foo must be static in api[Foo]
         |    type FooApi = congeal.api[Foo]
         |                          ^
         |one error found
      |""".stripMargin)
  }

  @Test
  def apiDoesNotCompileOnTraitWithNonPrivateThisInnerClasses() {
    compilingSourceErrorsWithMessage(
      """|object Test extends App {
         |  trait Foo { trait Bar }
         |  type FooApi = congeal.api[Foo]
         |}
      |""".stripMargin,
      """|source.scala:3: error: Foo must not have non-private[this] inner classes in api[Foo]
         |  type FooApi = congeal.api[Foo]
         |                        ^
         |one error found
      |""".stripMargin)
  }

  val selfReferencingMemberErrorMessage =
    """|source.scala:3: error: Foo must not have self-referencing members in api[Foo]
       |  type FooApi = congeal.api[Foo]
       |                        ^
       |one error found
    |""".stripMargin

  @Test
  def apiDoesNotCompileOnTraitWithSelfReferencingMembers1() {
    compilingSourceErrorsWithMessage(
      """|object Test extends App {
         |  trait Foo { def bar: Foo }
         |  type FooApi = congeal.api[Foo]
         |}
      |""".stripMargin,
      selfReferencingMemberErrorMessage)
  }

  @Test
  def apiDoesNotCompileOnTraitWithSelfReferencingMembers2() {
    compilingSourceErrorsWithMessage(
      """|object Test extends App {
         |  trait Foo { def bar(f: Foo): Unit }
         |  type FooApi = congeal.api[Foo]
         |}
      |""".stripMargin,
      selfReferencingMemberErrorMessage)
  }

  @Test
  def apiDoesNotCompileOnTraitWithSelfReferencingMembers3() {
    compilingSourceErrorsWithMessage(
      """|object Test extends App {
         |  trait Foo { val bar: Foo = null }
         |  type FooApi = congeal.api[Foo]
         |}
      |""".stripMargin,
      selfReferencingMemberErrorMessage)
  }

  @Test
  def apiDoesNotCompileOnTraitWithSelfReferencingMembers4() {
    compilingSourceErrorsWithMessage(
      """|object Test extends App {
         |  trait Foo { var bar: Foo = null }
         |  type FooApi = congeal.api[Foo]
         |}
      |""".stripMargin,
      selfReferencingMemberErrorMessage)
  }

}
