package congeal

import congeal.sc._

import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/** JUnit tests to make sure the `congeal.hasDependency` macro constrains its type parameter as
  * expected by rejecting (emitting error on) type parameters that do not match constraints.
  */
@RunWith(classOf[JUnit4])
class HasDependencyConstrainsTypeParameterTest {

  @Test
  def hasDependencyCompilesOnTrait() {
    compilingSourceSucceeds(
      """|object Test extends App {
         |  trait Foo
         |  type FooHasDependency = congeal.hasDependency[Foo]
         |}
      |""".stripMargin)
  }

  @Test
  def hasDependencyDoesNotCompileOnClass() {
    compilingSourceErrorsWithMessage(
      """|object Test extends App {
         |  class Foo
         |  type FooHasDependency = congeal.hasDependency[Foo]
         |}
      |""".stripMargin,
      """|source.scala:3: error: Foo must be a trait in hasDependency[Foo]
         |  type FooHasDependency = congeal.hasDependency[Foo]
         |                                  ^
         |one error found
      |""".stripMargin)
  }

  @Test
  def hasDependencyDoesNotCompileOnTraitInsideTrait() {
    compilingSourceErrorsWithMessage(
      """|trait A {
         |  trait Foo
         |  type FooHasDependency = congeal.hasDependency[Foo]
         |}
      |""".stripMargin,
      """|source.scala:3: error: Foo must be static in hasDependency[Foo]
         |  type FooHasDependency = congeal.hasDependency[Foo]
         |                                  ^
         |one error found
      |""".stripMargin)
  }

  @Test
  def hasDependencyDoesNotCompileOnTraitInsideMethod() {
    compilingSourceErrorsWithMessage(
      """|trait T {
         |  def a {
         |    trait Foo
         |    type FooHasDependency = congeal.hasDependency[Foo]
         |  }
         |}
      |""".stripMargin,
      """|source.scala:4: error: Foo must be static in hasDependency[Foo]
         |    type FooHasDependency = congeal.hasDependency[Foo]
         |                                    ^
         |one error found
      |""".stripMargin)
  }

  @Test
  def hasDependencyDoesNotCompileOnTraitWithNonPrivateThisInnerClasses() {
    compilingSourceErrorsWithMessage(
      """|object Test extends App {
         |  trait Foo { trait Bar }
         |  type FooHasDependency = congeal.hasDependency[Foo]
         |}
      |""".stripMargin,
      """|source.scala:3: error: Foo must not have non-private[this] inner classes in hasDependency[Foo]
         |  type FooHasDependency = congeal.hasDependency[Foo]
         |                                  ^
         |one error found
      |""".stripMargin)
  }

  val selfReferencingMemberErrorMessage =
    """|source.scala:3: error: Foo must not have self-referencing members in hasDependency[Foo]
       |  type FooHasDependency = congeal.hasDependency[Foo]
       |                                  ^
       |one error found
    |""".stripMargin

  @Test
  def hasDependencyDoesNotCompileOnTraitWithSelfReferencingMembers1() {
    compilingSourceErrorsWithMessage(
      """|object Test extends App {
         |  trait Foo { def bar: Foo }
         |  type FooHasDependency = congeal.hasDependency[Foo]
         |}
      |""".stripMargin,
      selfReferencingMemberErrorMessage)
  }

  @Test
  def hasDependencyDoesNotCompileOnTraitWithSelfReferencingMembers2() {
    compilingSourceErrorsWithMessage(
      """|object Test extends App {
         |  trait Foo { def bar(f: Foo): Unit }
         |  type FooHasDependency = congeal.hasDependency[Foo]
         |}
      |""".stripMargin,
      selfReferencingMemberErrorMessage)
  }

  @Test
  def hasDependencyDoesNotCompileOnTraitWithSelfReferencingMembers3() {
    compilingSourceErrorsWithMessage(
      """|object Test extends App {
         |  trait Foo { val bar: Foo = null }
         |  type FooHasDependency = congeal.hasDependency[Foo]
         |}
      |""".stripMargin,
      selfReferencingMemberErrorMessage)
  }

  @Test
  def hasDependencyDoesNotCompileOnTraitWithSelfReferencingMembers4() {
    compilingSourceErrorsWithMessage(
      """|object Test extends App {
         |  trait Foo { var bar: Foo = null }
         |  type FooHasDependency = congeal.hasDependency[Foo]
         |}
      |""".stripMargin,
      selfReferencingMemberErrorMessage)
  }

}
