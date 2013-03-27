package congeal

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/** JUnit tests for `congeal.api` macro. */
@RunWith(classOf[JUnit4])
class ApiTest {

  @Test
  def congealApiSaysHello() {
    val goo = new AnyRef with congeal.api
    goo.hello
  }

}
