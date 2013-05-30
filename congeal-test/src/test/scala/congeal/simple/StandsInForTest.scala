package congeal.simple

import congeal.sc._

import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/** JUnit tests for basic usage of the `congeal.standsInFor` macro. */
@RunWith(classOf[JUnit4])
class StandsInForTest {

  @Test
  def standsInForLeafComponent() {
    compilingSourceProducesAppWithOutput(
      """|import congeal._
         |object Test extends App {
         |  case class U(uName: String)
         |  trait URepository {
         |    def getU(uName: String): Option[U] = Some(U("from URepository"))
         |  }
         |  trait AltURepository extends standsInFor[URepository] {
         |    def getU(uName: String): Option[U] = Some(U("from AltURepository"))
         |  }
         |  trait UService extends hasDependency[URepository] {
         |    def getU(uName: String): Option[U] = uRepository.getU(uName)
         |  }
         |  val uService = new UService with componentImpl[AltURepository]
         |  println(uService.getU("testUName"))
         |}
      |""".stripMargin,
      "Test",
      "Some(U(from AltURepository))\n")
  }

  @Test
  def standsInForNonLeafComponent() {
    compilingSourceProducesAppWithOutput(
      """|import congeal._
         |object Test extends App {
         |  case class U(uName: String)
         |  trait URepository {
         |    def getU(uName: String): Option[U] = Some(U("from URepository"))
         |  }
         |  trait AltURepository extends standsInFor[URepository] {
         |    def getU(uName: String): Option[U] = Some(U("from AltURepository"))
         |  }
         |  trait UService extends hasDependency[URepository] {
         |    def getU(uName: String): Option[U] = uRepository.getU(uName)
         |  }
         |  trait Root extends hasPart[URepository] with hasPart[UService]
         |  trait AltRoot extends standsInFor[Root] with hasPart[AltURepository] with hasPart[UService]
         |  trait Application extends componentApi[Root] {
         |    println(uService.getU("testUName"))
         |  }
         |  new Application with componentImpl[Root]
         |  new Application with componentImpl[AltRoot]
         |}
      |""".stripMargin,
      "Test",
      "Some(U(from URepository))\nSome(U(from AltURepository))\n")
  }

}
