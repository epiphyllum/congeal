package congeal.simple

import congeal.sc._

import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/** JUnit tests to make sure the `congeal.hasPart` macro constrains its type parameter as
  * expected by rejecting (emitting error on) type parameters that do not match constraints.
  */
@RunWith(classOf[JUnit4])
class HasPartConstrainsTypeParameterTest {

  @Test
  def hasPartCompilesOnTrait() {
    compilingSourceSucceeds(
      """|object Test extends App {
         |  trait Foo
         |  type FooHasPart = congeal.hasPart[Foo]
         |}
      |""".stripMargin)
  }

  @Test
  def hasPartDoesNotCompileOnClass() {
    compilingSourceErrorsWithMessage(
      """|object Test extends App {
         |  class Foo
         |  type FooHasPart = congeal.hasPart[Foo]
         |}
      |""".stripMargin,
      """|source.scala:3: error: Foo must be a trait in hasPart[Foo]
         |  type FooHasPart = congeal.hasPart[Foo]
         |                            ^
         |one error found
      |""".stripMargin)
  }

  @Test
  def hasPartDoesNotCompileOnTraitInsideTrait() {
    compilingSourceErrorsWithMessage(
      """|trait A {
         |  trait Foo
         |  type FooHasPart = congeal.hasPart[Foo]
         |}
      |""".stripMargin,
      """|source.scala:3: error: Foo must be static in hasPart[Foo]
         |  type FooHasPart = congeal.hasPart[Foo]
         |                            ^
         |one error found
      |""".stripMargin)
  }

  @Test
  def hasPartDoesNotCompileOnTraitInsideMethod() {
    compilingSourceErrorsWithMessage(
      """|trait T {
         |  def a {
         |    trait Foo
         |    type FooHasPart = congeal.hasPart[Foo]
         |  }
         |}
      |""".stripMargin,
      """|source.scala:4: error: Foo must be static in hasPart[Foo]
         |    type FooHasPart = congeal.hasPart[Foo]
         |                              ^
         |one error found
      |""".stripMargin)
  }

  @Test
  def hasPartDoesNotCompileOnTraitWithNonPrivateThisInnerClasses() {
    compilingSourceErrorsWithMessage(
      """|object Test extends App {
         |  trait Foo { trait Bar }
         |  type FooHasPart = congeal.hasPart[Foo]
         |}
      |""".stripMargin,
      """|source.scala:3: error: Foo must not have non-private[this] inner classes in hasPart[Foo]
         |  type FooHasPart = congeal.hasPart[Foo]
         |                            ^
         |one error found
      |""".stripMargin)
  }

  val selfReferencingMemberErrorMessage =
    """|source.scala:3: error: Foo must not have self-referencing members in hasPart[Foo]
       |  type FooHasPart = congeal.hasPart[Foo]
       |                            ^
       |one error found
    |""".stripMargin

  @Test
  def hasPartDoesNotCompileOnTraitWithSelfReferencingMembers1() {
    compilingSourceErrorsWithMessage(
      """|object Test extends App {
         |  trait Foo { def bar: Foo }
         |  type FooHasPart = congeal.hasPart[Foo]
         |}
      |""".stripMargin,
      selfReferencingMemberErrorMessage)
  }

  @Test
  def hasPartDoesNotCompileOnTraitWithSelfReferencingMembers2() {
    compilingSourceErrorsWithMessage(
      """|object Test extends App {
         |  trait Foo { def bar(f: Foo): Unit }
         |  type FooHasPart = congeal.hasPart[Foo]
         |}
      |""".stripMargin,
      selfReferencingMemberErrorMessage)
  }

  @Test
  def hasPartDoesNotCompileOnTraitWithSelfReferencingMembers3() {
    compilingSourceErrorsWithMessage(
      """|object Test extends App {
         |  trait Foo { val bar: Foo = null }
         |  type FooHasPart = congeal.hasPart[Foo]
         |}
      |""".stripMargin,
      selfReferencingMemberErrorMessage)
  }

  @Test
  def hasPartDoesNotCompileOnTraitWithSelfReferencingMembers4() {
    compilingSourceErrorsWithMessage(
      """|object Test extends App {
         |  trait Foo { var bar: Foo = null }
         |  type FooHasPart = congeal.hasPart[Foo]
         |}
      |""".stripMargin,
      selfReferencingMemberErrorMessage)
  }

}
