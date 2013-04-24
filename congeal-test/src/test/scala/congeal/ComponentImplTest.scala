package congeal

import congeal.sc._

import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/** JUnit tests for `congeal.componentImpl` macro. */
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
  def componentImplWorksInNonDefaultPackage() {
    compilingSourceProducesAppWithOutput(
      """|package foo.bar
         |import congeal._
         |object Test extends App {
         |  case class U(uName: String)
         |  trait URepository {
         |     def getU(uName: String): Option[U] = None // STUB
         |  }
         |  class URepositoryComponentImpl extends componentApi[URepository] {
         |    override lazy val uRepository = new simpleImpl[URepository]
         |  }
         |  val uRepositoryComponent: componentApi[URepository] = new componentImpl[URepository] {}
         |  println(uRepositoryComponent.uRepository.getU("testUName"))
         |}
      |""".stripMargin,
      "foo.bar.Test",
      "None\n")
  }

}
