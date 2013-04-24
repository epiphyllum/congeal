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
    val urci: componentApi[URepository] = new componentImpl[URepository] {}
    println(urci.uRepository.getU("testUName"))
  }
  
}
