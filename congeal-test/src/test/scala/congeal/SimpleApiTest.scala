package congeal

import congeal.sc._

import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/** JUnit tests for `congeal.simpleApi` macro. */
@RunWith(classOf[JUnit4])
class SimpleApiTest {

  // TODO: tests need to run in parallel

  @Test
  def simpleApiSaysHello() {
    compilingSourceProducesAppWithOutput(
      """|import congeal.simpleApi
         |object Test extends App {
         |  case class U(uName: String)
         |  class URepository {
         |     def getU(uName: String): Option[U] = None // STUB
         |  }
         |  class URepositoryImpl extends URepository with simpleApi[URepository]
         |  val uRepository: simpleApi[URepository] = new URepositoryImpl
         |  println(uRepository.getU("testUName"))
         |}
      |""".stripMargin,
      "Test",
      "None\n")
  }

}
