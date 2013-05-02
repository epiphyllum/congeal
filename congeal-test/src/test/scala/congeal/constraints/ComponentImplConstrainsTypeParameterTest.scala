package congeal.constraints

// TODO: clean up massive repitition in these tests

import congeal.sc._

import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/** JUnit tests to make sure the `congeal.componentImpl` macro constrains its type parameter as
  * expected by rejecting (emitting error on) type parameters that do not match constraints.
  */
@RunWith(classOf[JUnit4])
class ComponentImplConstrainsTypeParameterTest {

  @Test
  def componentImplCompilesOnTrait() {
    compilingSourceSucceeds(
      """|object Test extends App {
         |  trait Foo
         |  type FooImpl = congeal.componentImpl[Foo]
         |}
      |""".stripMargin)
  }

  @Test
  def componentImplDoesNotCompileOnClass() {
    compilingSourceErrorsWithMessage(
      """|object Test extends App {
         |  class Foo
         |  type FooImpl = congeal.componentImpl[Foo]
         |}
      |""".stripMargin,
      """|source.scala:3: error: Foo must be a trait in componentImpl[Foo]
         |  type FooImpl = congeal.componentImpl[Foo]
         |                         ^
         |one error found
      |""".stripMargin)
  }

  @Test
  def componentImplDoesNotCompileOnTraitInsideTrait() {
    compilingSourceErrorsWithMessage(
      """|trait A {
         |  trait Foo
         |  type FooImpl = congeal.componentImpl[Foo]
         |}
      |""".stripMargin,
      """|source.scala:3: error: Foo must be static in componentImpl[Foo]
         |  type FooImpl = congeal.componentImpl[Foo]
         |                         ^
         |one error found
      |""".stripMargin)
  }

  @Test
  def componentImplDoesNotCompileOnTraitInsideMethod() {
    compilingSourceErrorsWithMessage(
      """|trait T {
         |  def a {
         |    trait Foo
         |    type FooImpl = congeal.componentImpl[Foo]
         |  }
         |}
      |""".stripMargin,
      """|source.scala:4: error: Foo must be static in componentImpl[Foo]
         |    type FooImpl = congeal.componentImpl[Foo]
         |                           ^
         |one error found
      |""".stripMargin)
  }

  @Test
  def componentImplDoesNotCompileOnTraitWithNonPrivateThisInnerClasses() {
    compilingSourceErrorsWithMessage(
      """|object Test extends App {
         |  trait Foo { trait Bar }
         |  type FooImpl = congeal.componentImpl[Foo]
         |}
      |""".stripMargin,
      """|source.scala:3: error: Foo must not have non-private[this] inner classes in componentImpl[Foo]
         |  type FooImpl = congeal.componentImpl[Foo]
         |                         ^
         |one error found
      |""".stripMargin)
  }

  val selfReferencingMemberErrorMessage =
    """|source.scala:3: error: Foo must not have self-referencing members in componentImpl[Foo]
       |  type FooImpl = congeal.componentImpl[Foo]
       |                         ^
       |one error found
    |""".stripMargin

  @Test
  def componentImplDoesNotCompileOnTraitWithSelfReferencingMembers1() {
    compilingSourceErrorsWithMessage(
      """|object Test extends App {
         |  trait Foo { def bar: Foo }
         |  type FooImpl = congeal.componentImpl[Foo]
         |}
      |""".stripMargin,
      selfReferencingMemberErrorMessage)
  }

  @Test
  def componentImplDoesNotCompileOnTraitWithSelfReferencingMembers2() {
    compilingSourceErrorsWithMessage(
      """|object Test extends App {
         |  trait Foo { def bar(f: Foo): Unit }
         |  type FooImpl = congeal.componentImpl[Foo]
         |}
      |""".stripMargin,
      selfReferencingMemberErrorMessage)
  }

  @Test
  def componentImplDoesNotCompileOnTraitWithSelfReferencingMembers3() {
    compilingSourceErrorsWithMessage(
      """|object Test extends App {
         |  trait Foo { val bar: Foo = null }
         |  type FooImpl = congeal.componentImpl[Foo]
         |}
      |""".stripMargin,
      selfReferencingMemberErrorMessage)
  }

  @Test
  def componentImplDoesNotCompileOnTraitWithSelfReferencingMembers4() {
    compilingSourceErrorsWithMessage(
      """|object Test extends App {
         |  trait Foo { var bar: Foo = null }
         |  type FooImpl = congeal.componentImpl[Foo]
         |}
      |""".stripMargin,
      selfReferencingMemberErrorMessage)
  }

}
