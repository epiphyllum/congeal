package congeal

import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/** Testbed for the thing i am testing at the moment. */
@RunWith(classOf[JUnit4])
class QuickTest {

  @Test
  def testQuick() {
    import congeal._
    trait UService extends hasDependency[URepository] {
      def getU(uName: String): Option[U] = uRepository.getU(uName)
    }
    val uService = new UService  with componentImpl[URepository]
    println(uService.getU("testUName"))
  }
  
}
