package congeal

// classes used as test inputs. used only in QuickTest

// this here is maybe congeal-examples/basic:

case class U(uName: String)

trait URepository {
  def getU(uName: String): Option[U] = None // STUB
}

trait UService extends hasDependency[URepository] {
  def getU(uName: String): Option[U] = uRepository.getU(uName)
}

// this doesn't work quite yet:
// trait Root extends URepository with UService

//trait RootComponentApi extends componentApi[URepository] with componentApi[UService]
//trait RootComponentImpl extends componentImpl[URepository] with componentImpl[UService]

// abstract class Application extends RootComponentApi {
//   println(uRepository.getU("testUName"))
//   println(uService.getU("testUName"))
// }

// trait TestTest {
//   new Application with RootComponentImpl
// }






// here lies stuff from next example:

case class S(sName: String)

case class T(tName: String)

trait SRepository {
  def getS(sName: String): Option[S] = None // STUB
}

trait TRepository {
  def getT(tName: String): Option[T] = None // STUB
}

// trait SRComponentApi {
//   def sr: SRApi
//   trait SRApi {
//     def getS(sName: String): Option[S]
//   }
// }

// trait TRComponentApi {
//   def tr: TRApi
//   trait TRApi {
//     def getT(tName: String): Option[T]
//   }
// }

// trait URComponentApi {
//   def ur: URApi
//   trait URApi {
//     def getU(uName: String): Option[U]
//   }
// }

// trait RootComponentApi extends
//   SRComponentApi with
//   TRComponentApi with
//   URComponentApi
