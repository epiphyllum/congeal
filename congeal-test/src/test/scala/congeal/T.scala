package congeal

// classes used as test inputs. used only in QuickTest

case class S(sName: String)

case class T(tName: String)

case class U(uName: String)

trait SRepository {
  def getS(sName: String): Option[S] = None // STUB
}

trait TRepository {
  def getT(tName: String): Option[T] = None // STUB
}

trait URepository {
  def getU(uName: String): Option[U] = None // STUB
}
