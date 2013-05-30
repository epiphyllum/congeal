package congeal

import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

import congeal._

/** testbed for the thing i am testing at the moment. */
@RunWith(classOf[JUnit4])
class QuickTest {

  @Test
  def testQuick() {

    import congeal.examples.flat._
    import congeal.examples.alternate._
    new Application with componentImpl[AltRoot2]
  }
  
}
