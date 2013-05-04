package congeal.simple

import congeal.sc._

import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/** JUnit tests for basic usage of the `congeal.impl` macro. */
@RunWith(classOf[JUnit4])
class ImplTest {

  @Test
  def implSaysHello() {
    compilingSourceProducesAppWithOutput(
      """|import congeal._
         |object Test extends App {
         |  case class U(uName: String)
         |  trait URepository {
         |     def getU(uName: String): Option[U] = None // STUB
         |  }
         |  val uRepository: api[URepository] = new impl[URepository] {}
         |  println(uRepository.getU("testUName"))
         |}
      |""".stripMargin,
      "Test",
      "None\n")
  }

}
