package congeal

import congeal.sc._

import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/** JUnit tests to make sure the `congeal.simpleApi` macro constrains its type parameter as
  * expected by rejecting (emitting error on) type parameters that do not match constraints.
  */
@RunWith(classOf[JUnit4])
class SimpleApiConstrainsTypeParameterTest {

  @Test
  def simpleApiCompilesOnTrait() {
    compilingSourceSucceeds(
      """|object Test extends App {
         |  trait Foo
         |  type FooApi = congeal.simpleApi[Foo]
         |}
      |""".stripMargin)
  }

  @Test
  def simpleApiCompilesOnClass() {
    compilingSourceSucceeds(
      """|object Test extends App {
         |  class Foo
         |  type FooApi = congeal.simpleApi[Foo]
         |}
      |""".stripMargin)
  }

  @Test
  def simpleApiDoesNotCompileOnTraitWithNonPrivateThisInnerClasses() {
    compilingSourceErrorsWithMessage(
      """|object Test extends App {
         |  trait Foo { trait Bar }
         |  type FooApi = congeal.simpleApi[Foo]
         |}
      |""".stripMargin,
      """|source.scala:3: error: Foo must not have non-private[this] inner classes in simpleApi[Foo]
         |  type FooApi = congeal.simpleApi[Foo]
         |                        ^
         |one error found
      |""".stripMargin)
  }

  val selfReferencingMemberErrorMessage =
    """|source.scala:3: error: Foo must not have self-referencing members in simpleApi[Foo]
       |  type FooApi = congeal.simpleApi[Foo]
       |                        ^
       |one error found
    |""".stripMargin

  @Test
  def simpleApiDoesNotCompileOnTraitWithSelfReferencingMembers1() {
    compilingSourceErrorsWithMessage(
      """|object Test extends App {
         |  trait Foo { def bar: Foo }
         |  type FooApi = congeal.simpleApi[Foo]
         |}
      |""".stripMargin,
      selfReferencingMemberErrorMessage)
  }

  @Test
  def simpleApiDoesNotCompileOnTraitWithSelfReferencingMembers2() {
    compilingSourceErrorsWithMessage(
      """|object Test extends App {
         |  trait Foo { def bar(f: Foo): Unit }
         |  type FooApi = congeal.simpleApi[Foo]
         |}
      |""".stripMargin,
      selfReferencingMemberErrorMessage)
  }

  @Test
  def simpleApiDoesNotCompileOnTraitWithSelfReferencingMembers3() {
    compilingSourceErrorsWithMessage(
      """|object Test extends App {
         |  trait Foo { val bar: Foo = null }
         |  type FooApi = congeal.simpleApi[Foo]
         |}
      |""".stripMargin,
      selfReferencingMemberErrorMessage)
  }

  @Test
  def simpleApiDoesNotCompileOnTraitWithSelfReferencingMembers4() {
    compilingSourceErrorsWithMessage(
      """|object Test extends App {
         |  trait Foo { var bar: Foo = null }
         |  type FooApi = congeal.simpleApi[Foo]
         |}
      |""".stripMargin,
      selfReferencingMemberErrorMessage)
  }

}
