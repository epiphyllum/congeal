package congeal

import congeal.sc._

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

import scala.tools.nsc.{ Main => Scalac }

/** JUnit tests for `congeal.api` macro. */
@RunWith(classOf[JUnit4])
class ApiTest {

  @Test
  def congealApiSaysHello() {
    class Foo { def bar = println("hi from foo") }
    val goo = new congeal.api[Foo]
    goo.bar

    println(compilingSource(""))
  }
  
}
