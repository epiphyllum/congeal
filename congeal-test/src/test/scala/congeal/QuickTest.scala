package congeal

import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/** JUnit tests for `congeal.api` macro. */
@RunWith(classOf[JUnit4])
class QuickTest {

  @Test
  def testQuick() {
    import congeal._
    class URCI extends componentApi[URepository] {
      override lazy val uRepository = new simpleImpl[URepository]
    }
    val urci = new URCI
    println(urci.uRepository.getU("testUName"))
  }
  
}
