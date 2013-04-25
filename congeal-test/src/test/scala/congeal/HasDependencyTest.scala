package congeal

import congeal.sc._

import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/** JUnit tests for `congeal.hasDependency` macro.
  *
  * Currently, only the most simple use-case is tested:
  * 
  * {{{
  * case class U(uName: String)
  * trait URepository {
  *   def getU(uName: String): Option[U] = None // STUB
  * }
  * trait UService extends hasDependency[URepository] {
  *   def getU(uName: String): Option[U] = uRepository.getU(uName)
  * }
  * }}}
  */
@RunWith(classOf[JUnit4])
class HasDependencyTest {

  @Test
  def hasDependencySaysHello() {
    compilingSourceProducesAppWithOutput(
      """|import congeal._
         |object Test extends App {
         |  case class U(uName: String)
         |  trait URepository {
         |    def getU(uName: String): Option[U] = None // STUB
         |  }
         |  trait UService extends hasDependency[URepository] {
         |    def getU(uName: String): Option[U] = uRepository.getU(uName)
         |  }
         |  val uService = new UService with componentImpl[URepository]
         |  println(uService.getU("testUName"))
         |}
      |""".stripMargin,
      "Test",
      "None\n")
  }

  @Test
  def simpleImplWorksInNonDefaultPackage() {
    compilingSourceProducesAppWithOutput(
      """|package foo.bar
         |import congeal._
         |object Test extends App {
         |  case class U(uName: String)
         |  trait URepository {
         |    def getU(uName: String): Option[U] = None // STUB
         |  }
         |  trait UService extends hasDependency[URepository] {
         |    def getU(uName: String): Option[U] = uRepository.getU(uName)
         |  }
         |  val uService = new UService with componentImpl[URepository]
         |  println(uService.getU("testUName"))
         |}
      |""".stripMargin,
      "foo.bar.Test",
      "None\n")
  }

}
