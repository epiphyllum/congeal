package congeal.examples.basic

import congeal._

// classes used as test inputs. used only in QuickTest

case class U(uName: String)

trait URepository {
  def getU(uName: String): Option[U] = None // STUB
}

trait UService extends hasDependency[URepository] {
  def getU(uName: String): Option[U] = uRepository.getU(uName)
}

trait Root extends hasPart[URepository] with hasPart[UService]

abstract class Application extends componentApi[Root] {
  println(uRepository.getU("testUName"))
  println(uService.getU("testUName"))
}

// outputs None/None
object Test extends App {
  new Application with componentImpl[Root]
}




// here lies stuff from next example:

case class S(sName: String)

case class T(tName: String)

trait SRepository {
  def getS(sName: String): Option[S] = None // STUB
}

trait TRepository {
  def getT(tName: String): Option[T] = None // STUB
}
