package congeal

import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/** testbed for the thing i am testing at the moment. */
@RunWith(classOf[JUnit4])
class QuickTest {

  @Test
  def testQuick() {
    import congeal.componentApi
    import congeal.examples.basic._
    trait Application extends componentApi[Root] {
      println(uRepository.getU("testUName"))
      println(uService.getU("testUName"))
    }
    new Application with componentImpl[Root]

  }
  
}
