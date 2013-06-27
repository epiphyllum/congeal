package congeal

import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

import congeal._

/** testbed for the thing i am testing at the moment. */
@RunWith(classOf[JUnit4])
class QuickTest2 {

  @Test
  def testQuick() {
    import congeal.examples.privateparts._
    type T = congeal.hidden.api.congeal.examples.privateparts.SFactory
    trait Foo
    val t = new Foo with componentImpl[SFactory] {}

    import com.softwaremill.debug.DebugConsole._
    val a = 5
    debugReport(t, T, a+7)
  }
  
}
