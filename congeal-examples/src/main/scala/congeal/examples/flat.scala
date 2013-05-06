package congeal.examples.flat

import congeal._

case class S(sName: String)

case class T(tName: String)

case class U(uName: String)

trait SRepository {
  def getS(sName: String): Option[S] = Some(S(sName))
}

trait TRepository {
  def getT(tName: String): Option[T] = Some(T(tName))
}

trait URepository {
  def getU(uName: String): Option[U] = Some(U(uName))
}

trait SService extends hasDependency[SRepository] {
  def getS(sName: String): Option[S] = sRepository.getS(sName)
}

trait TService extends hasDependency[TRepository] {
  def getT(tName: String): Option[T] = tRepository.getT(tName)
}

trait UService extends hasDependency[URepository] {
  def getU(uName: String): Option[U] = uRepository.getU(uName)
}

trait Root extends
  hasPart[SRepository] with
  hasPart[TRepository] with
  hasPart[URepository] with
  hasPart[SService] with
  hasPart[TService] with
  hasPart[UService]

abstract class Application extends componentApi[Root] {
  println(sRepository.getS("testSName"))
  println(sService.getS("testSName"))
  println(tRepository.getT("testTName"))
  println(tService.getT("testTName"))
  println(uRepository.getU("testUName"))
  println(uService.getU("testUName"))
}

/* outputs
Some(S(testSName))
Some(S(testSName))
Some(T(testTName))
Some(T(testTName))
Some(U(testUName))
Some(U(testUName))
*/
object ApplicationTest extends App {
  new Application with componentImpl[Root]
}
