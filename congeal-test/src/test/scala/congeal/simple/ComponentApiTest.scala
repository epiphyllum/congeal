package congeal.simple

import congeal.sc._

import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/** JUnit tests for `congeal.componentApi` macro. */
@RunWith(classOf[JUnit4])
class ComponentApiTest {

  @Test
  def componentApiSaysHello() {
    compilingSourceProducesAppWithOutput(
      """|import congeal._
         |object Test extends App {
         |  case class U(uName: String)
         |  trait URepository {
         |     def getU(uName: String): Option[U] = None // STUB
         |  }
         |  class URepositoryComponentImpl extends componentApi[URepository] {
         |    override lazy val uRepository = new impl[URepository] {}
         |  }
         |  val uRepositoryComponent: componentApi[URepository] = new URepositoryComponentImpl
         |  println(uRepositoryComponent.uRepository.getU("testUName"))
         |}
      |""".stripMargin,
      "Test",
      "None\n")
  }

  @Test
  def componentApiWorksInNonDefaultPackage() {
    compilingSourceProducesAppWithOutput(
      """|package foo.bar
         |import congeal._
         |object Test extends App {
         |  case class U(uName: String)
         |  trait URepository {
         |     def getU(uName: String): Option[U] = None // STUB
         |  }
         |  class URepositoryComponentImpl extends componentApi[URepository] {
         |    override lazy val uRepository = new impl[URepository] {}
         |  }
         |  val uRepositoryComponent: componentApi[URepository] = new URepositoryComponentImpl
         |  println(uRepositoryComponent.uRepository.getU("testUName"))
         |}
      |""".stripMargin,
      "foo.bar.Test",
      "None\n")
  }

  @Test
  def componentApiDoesNotProvideInjectionForEmptyTrait() {
    compilingSourceErrorsWithMessage(
      """|import congeal._
         |object Test extends App {
         |  trait Foo {}
         |  trait Bar extends componentApi[Foo] {
         |    println(foo)
         |  }
         |}
      |""".stripMargin,
      """|source.scala:5: error: not found: value foo
         |    println(foo)
         |            ^
         |one error found
      |""".stripMargin)

  }
}
