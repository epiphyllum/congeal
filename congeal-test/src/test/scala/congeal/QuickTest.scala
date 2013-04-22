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
    import congeal.simpleApi
    class URepositoryImpl extends URepository with simpleApi[URepository]
    val uRepository: simpleApi[URepository] = new URepositoryImpl
    println(uRepository.getU("testUName"))
  }
  
}
