package congeal

import congeal.sc._

import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/** JUnit tests for `congeal.simpleImpl` macro. */
@RunWith(classOf[JUnit4])
class SimpleImplTest {

  // TODO: tests need to run in parallel
  // TODO: test with protected methods
  // TODO: test with private methods
  // TODO: test with private[this] methods
  // TODO: test with inheritance

  @Test
  def simpleImplSaysHello() {
    compilingSourceProducesAppWithOutput(
      """|import congeal._
         |object Test extends App {
         |  case class U(uName: String)
         |  trait URepository {
         |     def getU(uName: String): Option[U] = None // STUB
         |  }
         |  val uRepository: simpleApi[URepository] = new simpleImpl[URepository]
         |  println(uRepository.getU("testUName"))
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
         |     def getU(uName: String): Option[U] = None // STUB
         |  }
         |  val uRepository: simpleApi[URepository] = new simpleImpl[URepository]
         |  println(uRepository.getU("testUName"))
         |}
      |""".stripMargin,
      "foo.bar.Test",
      "None\n")
  }

}
