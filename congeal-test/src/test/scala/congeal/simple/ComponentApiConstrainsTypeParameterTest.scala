package congeal.simple

import congeal.sc._

import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/** JUnit tests to make sure the `congeal.componentApi` macro constrains its type parameter as
  * expected by rejecting (emitting error on) type parameters that do not match constraints.
  */
@RunWith(classOf[JUnit4])
class ComponentApiConstrainsTypeParameterTest {

  @Test
  def componentApiCompilesOnTrait() {
    compilingSourceSucceeds(
      """|object Test extends App {
         |  trait Foo
         |  type FooApi = congeal.componentApi[Foo]
         |}
      |""".stripMargin)
  }

  @Test
  def componentApiDoesNotCompileOnClass() {
    compilingSourceErrorsWithMessage(
      """|object Test extends App {
         |  class Foo
         |  type FooApi = congeal.componentApi[Foo]
         |}
      |""".stripMargin,
      """|source.scala:3: error: Foo must be a trait in componentApi[Foo]
         |  type FooApi = congeal.componentApi[Foo]
         |                        ^
         |one error found
      |""".stripMargin)
  }

  @Test
  def componentApiDoesNotCompileOnTraitInsideTrait() {
    compilingSourceErrorsWithMessage(
      """|trait A {
         |  trait Foo
         |  type FooApi = congeal.componentApi[Foo]
         |}
      |""".stripMargin,
      """|source.scala:3: error: Foo must be static in componentApi[Foo]
         |  type FooApi = congeal.componentApi[Foo]
         |                        ^
         |one error found
      |""".stripMargin)
  }

  @Test
  def componentApiDoesNotCompileOnTraitInsideMethod() {
    compilingSourceErrorsWithMessage(
      """|trait T {
         |  def a {
         |    trait Foo
         |    type FooApi = congeal.componentApi[Foo]
         |  }
         |}
      |""".stripMargin,
      """|source.scala:4: error: Foo must be static in componentApi[Foo]
         |    type FooApi = congeal.componentApi[Foo]
         |                          ^
         |one error found
      |""".stripMargin)
  }

  @Test
  def componentApiDoesNotCompileOnTraitWithNonPrivateThisInnerClasses() {
    compilingSourceErrorsWithMessage(
      """|object Test extends App {
         |  trait Foo { trait Bar }
         |  type FooApi = congeal.componentApi[Foo]
         |}
      |""".stripMargin,
      """|source.scala:3: error: Foo must not have non-private[this] inner classes in componentApi[Foo]
         |  type FooApi = congeal.componentApi[Foo]
         |                        ^
         |one error found
      |""".stripMargin)
  }

  val selfReferencingMemberErrorMessage =
    """|source.scala:3: error: Foo must not have self-referencing members in componentApi[Foo]
       |  type FooApi = congeal.componentApi[Foo]
       |                        ^
       |one error found
    |""".stripMargin

  @Test
  def componentApiDoesNotCompileOnTraitWithSelfReferencingMembers1() {
    compilingSourceErrorsWithMessage(
      """|object Test extends App {
         |  trait Foo { def bar: Foo }
         |  type FooApi = congeal.componentApi[Foo]
         |}
      |""".stripMargin,
      selfReferencingMemberErrorMessage)
  }

  @Test
  def componentApiDoesNotCompileOnTraitWithSelfReferencingMembers2() {
    compilingSourceErrorsWithMessage(
      """|object Test extends App {
         |  trait Foo { def bar(f: Foo): Unit }
         |  type FooApi = congeal.componentApi[Foo]
         |}
      |""".stripMargin,
      selfReferencingMemberErrorMessage)
  }

  @Test
  def componentApiDoesNotCompileOnTraitWithSelfReferencingMembers3() {
    compilingSourceErrorsWithMessage(
      """|object Test extends App {
         |  trait Foo { val bar: Foo = null }
         |  type FooApi = congeal.componentApi[Foo]
         |}
      |""".stripMargin,
      selfReferencingMemberErrorMessage)
  }

  @Test
  def componentApiDoesNotCompileOnTraitWithSelfReferencingMembers4() {
    compilingSourceErrorsWithMessage(
      """|object Test extends App {
         |  trait Foo { var bar: Foo = null }
         |  type FooApi = congeal.componentApi[Foo]
         |}
      |""".stripMargin,
      selfReferencingMemberErrorMessage)
  }

}
