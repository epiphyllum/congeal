package congeal

import congeal.sc.compilingSource

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/** JUnit tests for `congeal.api` macro. */
@RunWith(classOf[JUnit4])
class ApiTest {

  @Test
  def congealApiSaysHello() {
    import congeal.api
    import congeal.C
    class Foo { def bar = println("hi from foo") }
    val goo = new api[Foo]
    goo.bar

    println(compilingSource("""
                            import congeal.api
                            import congeal.C
                            class Foo { def bar = println("hi from foo") }
                            val goo = new api[Foo]
                            goo.bar
                            """))
  }
  
}
