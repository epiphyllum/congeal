package congeal.simple

import congeal.sc._

import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/** JUnit tests for basic usage of the `congeal.componentImpl` macro. */
@RunWith(classOf[JUnit4])
class ComponentImplTest {

  @Test
  def componentImplSaysHello() {
    compilingSourceProducesAppWithOutput(
      """|import congeal._
         |object Test extends App {
         |  case class U(uName: String)
         |  trait URepository {
         |     def getU(uName: String): Option[U] = None // STUB
         |  }
         |  val uRepositoryComponent: componentApi[URepository] = new componentImpl[URepository] {}
         |  println(uRepositoryComponent.uRepository.getU("testUName"))
         |}
      |""".stripMargin,
      "Test",
      "None\n")
  }

  @Test
  def componentImplDoesNotProvideInjectionForEmptyTrait() {
    compilingSourceErrorsWithMessage(
      """|import congeal._
         |object Test extends App {
         |  trait Foo {}
         |  trait Bar extends componentImpl[Foo] {
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
