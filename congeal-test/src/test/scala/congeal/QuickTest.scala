package congeal

import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

import congeal._

case class U(uName: String)
trait URepository {
  def getU(uName: String): Option[U] = None // STUB
}
trait Root extends hasPart[URepository]

/** testbed for the thing i am testing at the moment. */
@RunWith(classOf[JUnit4])
class QuickTest {

  @Test
  def testQuick() {
    val root: componentApi[Root] = new componentImpl[Root] {
      println(uRepository.getU("testUName"))
    }
  }
  
}
