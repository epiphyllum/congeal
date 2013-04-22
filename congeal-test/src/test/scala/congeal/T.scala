package congeal

// classes used as test inputs. used only in QuickTest

case class S(sName: String)

case class T(tName: String)

case class U(uName: String)

class SRepository {
  def getS(sName: String): Option[S] = None // STUB
}

class TRepository {
  def getT(tName: String): Option[T] = None // STUB
}

class URepository {
  def getU(uName: String): Option[U] = None // STUB
}
