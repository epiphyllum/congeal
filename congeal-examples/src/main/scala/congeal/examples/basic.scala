package congeal.examples.basic

import congeal._

case class U(uName: String)

trait URepository {
  def getU(uName: String): Option[U] = Some(U(uName))
}

trait UService extends hasDependency[URepository] {
  def getU(uName: String): Option[U] = uRepository.getU(uName)
}

trait Root extends hasPart[URepository] with hasPart[UService]

abstract class Application extends componentApi[Root] {
  println(uRepository.getU("testUName"))
  println(uService.getU("testUName"))
}

/* outputs
Some(U(testUName))
Some(U(testUName))
*/
object ApplicationTest extends App {
  new Application with componentImpl[Root]
}
