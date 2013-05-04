package congeal.simple

import congeal.sc._

import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/** JUnit tests for basic usage of the `congeal.hasPart` macro. */
@RunWith(classOf[JUnit4])
class HasPartTest {

  @Test
  def hasPartSaysHello() {
    compilingSourceProducesAppWithOutput(
      """|import congeal._
         |  case class U(uName: String)
         |  trait URepository {
         |    def getU(uName: String): Option[U] = None // STUB
         |  }
         |  trait Root extends hasPart[URepository]
         |object Test extends App {
         |  val root: componentApi[Root] = new componentImpl[Root] {
         |    println(uRepository.getU("testUName"))
         |  }
         |}
      |""".stripMargin,
      "Test",
      "None\n")
  }

}
