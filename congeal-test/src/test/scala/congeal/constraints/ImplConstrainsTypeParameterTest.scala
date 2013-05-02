package congeal.constraints

import congeal.sc._

import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/** JUnit tests to make sure the `congeal.impl` macro constrains its type parameter as
  * expected by rejecting (emitting error on) type parameters that do not match constraints.
  */
@RunWith(classOf[JUnit4])
class ImplConstrainsTypeParameterTest {

  @Test
  def implCompilesOnTrait() {
    compilingSourceSucceeds(
      """|object Test extends App {
         |  trait Foo
         |  type FooApi = congeal.impl[Foo]
         |}
      |""".stripMargin)
  }

  @Test
  def implDoesNotCompileOnClass() {
    compilingSourceErrorsWithMessage(
      """|object Test extends App {
         |  class Foo
         |  type FooApi = congeal.impl[Foo]
         |}
      |""".stripMargin,
      """|source.scala:3: error: Foo must be a trait in impl[Foo]
         |  type FooApi = congeal.impl[Foo]
         |                        ^
         |one error found
      |""".stripMargin)
  }

  @Test
  def implDoesNotCompileOnTraitInsideTrait() {
    compilingSourceErrorsWithMessage(
      """|trait A {
         |  trait Foo
         |  type FooApi = congeal.impl[Foo]
         |}
      |""".stripMargin,
      """|source.scala:3: error: Foo must be static in impl[Foo]
         |  type FooApi = congeal.impl[Foo]
         |                        ^
         |one error found
      |""".stripMargin)
  }

  @Test
  def implDoesNotCompileOnTraitInsideMethod() {
    compilingSourceErrorsWithMessage(
      """|trait T {
         |  def a {
         |    trait Foo
         |    type FooApi = congeal.impl[Foo]
         |  }
         |}
      |""".stripMargin,
      """|source.scala:4: error: Foo must be static in impl[Foo]
         |    type FooApi = congeal.impl[Foo]
         |                          ^
         |one error found
      |""".stripMargin)
  }

  @Test
  def implDoesNotCompileOnTraitWithNonPrivateThisInnerClasses() {
    compilingSourceErrorsWithMessage(
      """|object Test extends App {
         |  trait Foo { trait Bar }
         |  type FooApi = congeal.impl[Foo]
         |}
      |""".stripMargin,
      """|source.scala:3: error: Foo must not have non-private[this] inner classes in impl[Foo]
         |  type FooApi = congeal.impl[Foo]
         |                        ^
         |one error found
      |""".stripMargin)
  }

  val selfReferencingMemberErrorMessage =
    """|source.scala:3: error: Foo must not have self-referencing members in impl[Foo]
       |  type FooApi = congeal.impl[Foo]
       |                        ^
       |one error found
    |""".stripMargin

  @Test
  def implDoesNotCompileOnTraitWithSelfReferencingMembers1() {
    compilingSourceErrorsWithMessage(
      """|object Test extends App {
         |  trait Foo { def bar: Foo }
         |  type FooApi = congeal.impl[Foo]
         |}
      |""".stripMargin,
      selfReferencingMemberErrorMessage)
  }

  @Test
  def implDoesNotCompileOnTraitWithSelfReferencingMembers2() {
    compilingSourceErrorsWithMessage(
      """|object Test extends App {
         |  trait Foo { def bar(f: Foo): Unit }
         |  type FooApi = congeal.impl[Foo]
         |}
      |""".stripMargin,
      selfReferencingMemberErrorMessage)
  }

  @Test
  def implDoesNotCompileOnTraitWithSelfReferencingMembers3() {
    compilingSourceErrorsWithMessage(
      """|object Test extends App {
         |  trait Foo { val bar: Foo = null }
         |  type FooApi = congeal.impl[Foo]
         |}
      |""".stripMargin,
      selfReferencingMemberErrorMessage)
  }

  @Test
  def implDoesNotCompileOnTraitWithSelfReferencingMembers4() {
    compilingSourceErrorsWithMessage(
      """|object Test extends App {
         |  trait Foo { var bar: Foo = null }
         |  type FooApi = congeal.impl[Foo]
         |}
      |""".stripMargin,
      selfReferencingMemberErrorMessage)
  }

}
