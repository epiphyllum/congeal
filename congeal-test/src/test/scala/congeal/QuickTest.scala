package congeal

import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/** JUnit tests for `congeal.api` macro. */
@RunWith(classOf[JUnit4])
class QuickTest {

  trait Foo { def bar: String = "Foo.bar" }
  trait FooApi { def bar: String }
  trait FooImpl extends Foo with FooApi


  // 
  @Test
  def testQuick() {

    object TestQ {
      trait Foo { def bar = println("hi from foo") }
      val goo = new simpleApi[Foo]
    }
    TestQ.goo.bar
  }
  
}
