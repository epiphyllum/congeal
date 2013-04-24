package congeal

import congeal.sc._

import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/** JUnit tests to make sure the `congeal.simpleImpl` macro constrains its type parameter as
  * expected by rejecting (emitting error on) type parameters that do not match constraints.
  */
@RunWith(classOf[JUnit4])
class SimpleImplConstrainsTypeParameterTest {

  @Test
  def simpleImplCompilesOnTrait() {
    compilingSourceSucceeds(
      """|object Test extends App {
         |  trait Foo
         |  type FooApi = congeal.simpleImpl[Foo]
         |}
      |""".stripMargin)
  }

  @Test
  def simpleImplDoesNotCompileOnClass() {
    compilingSourceErrorsWithMessage(
      """|object Test extends App {
         |  class Foo
         |  type FooApi = congeal.simpleImpl[Foo]
         |}
      |""".stripMargin,
      """|source.scala:3: error: Foo must be a trait in simpleImpl[Foo]
         |  type FooApi = congeal.simpleImpl[Foo]
         |                        ^
         |one error found
      |""".stripMargin)
  }

  @Test
  def simpleImplDoesNotCompileOnTraitInsideTrait() {
    compilingSourceErrorsWithMessage(
      """|trait A {
         |  trait Foo
         |  type FooApi = congeal.simpleImpl[Foo]
         |}
      |""".stripMargin,
      """|source.scala:3: error: Foo must be static in simpleImpl[Foo]
         |  type FooApi = congeal.simpleImpl[Foo]
         |                        ^
         |one error found
      |""".stripMargin)
  }

  @Test
  def simpleImplDoesNotCompileOnTraitInsideMethod() {
    compilingSourceErrorsWithMessage(
      """|trait T {
         |  def a {
         |    trait Foo
         |    type FooApi = congeal.simpleImpl[Foo]
         |  }
         |}
      |""".stripMargin,
      """|source.scala:4: error: Foo must be static in simpleImpl[Foo]
         |    type FooApi = congeal.simpleImpl[Foo]
         |                          ^
         |one error found
      |""".stripMargin)
  }

  @Test
  def simpleImplDoesNotCompileOnTraitWithNonPrivateThisInnerClasses() {
    compilingSourceErrorsWithMessage(
      """|object Test extends App {
         |  trait Foo { trait Bar }
         |  type FooApi = congeal.simpleImpl[Foo]
         |}
      |""".stripMargin,
      """|source.scala:3: error: Foo must not have non-private[this] inner classes in simpleImpl[Foo]
         |  type FooApi = congeal.simpleImpl[Foo]
         |                        ^
         |one error found
      |""".stripMargin)
  }

  val selfReferencingMemberErrorMessage =
    """|source.scala:3: error: Foo must not have self-referencing members in simpleImpl[Foo]
       |  type FooApi = congeal.simpleImpl[Foo]
       |                        ^
       |one error found
    |""".stripMargin

  @Test
  def simpleImplDoesNotCompileOnTraitWithSelfReferencingMembers1() {
    compilingSourceErrorsWithMessage(
      """|object Test extends App {
         |  trait Foo { def bar: Foo }
         |  type FooApi = congeal.simpleImpl[Foo]
         |}
      |""".stripMargin,
      selfReferencingMemberErrorMessage)
  }

  @Test
  def simpleImplDoesNotCompileOnTraitWithSelfReferencingMembers2() {
    compilingSourceErrorsWithMessage(
      """|object Test extends App {
         |  trait Foo { def bar(f: Foo): Unit }
         |  type FooApi = congeal.simpleImpl[Foo]
         |}
      |""".stripMargin,
      selfReferencingMemberErrorMessage)
  }

  @Test
  def simpleImplDoesNotCompileOnTraitWithSelfReferencingMembers3() {
    compilingSourceErrorsWithMessage(
      """|object Test extends App {
         |  trait Foo { val bar: Foo = null }
         |  type FooApi = congeal.simpleImpl[Foo]
         |}
      |""".stripMargin,
      selfReferencingMemberErrorMessage)
  }

  @Test
  def simpleImplDoesNotCompileOnTraitWithSelfReferencingMembers4() {
    compilingSourceErrorsWithMessage(
      """|object Test extends App {
         |  trait Foo { var bar: Foo = null }
         |  type FooApi = congeal.simpleImpl[Foo]
         |}
      |""".stripMargin,
      selfReferencingMemberErrorMessage)
  }

}
