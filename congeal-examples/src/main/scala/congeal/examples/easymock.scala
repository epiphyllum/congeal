package congeal.examples.easymock

import congeal._
import congeal.examples.flat._

import org.junit.Assert.assertEquals
import org.easymock.{ EasyMock => easy }

trait MockRoot extends easyMock[Root]

trait UServiceSpec extends
  componentImpl[MockRoot] with
  componentImpl[UService] {
  val uName = "asdf"
  val uOption = Some(U("jkl;"))
  easy.expect(uRepository.getU(uName)).andReturn(uOption)
  easy.replay(uRepository)
  val result = uService.getU(uName)
  println(result)
  assertEquals(result, uOption)
}

// also works:

trait URepositoryMock extends easyMock[URepository]

trait UServiceSpec2 extends
  componentImpl[URepositoryMock] with
  componentImpl[UService] {
  val uName = "asdf"
  val uOption = Some(U("jkl;"))
  easy.expect(uRepository.getU(uName)).andReturn(uOption)
  easy.replay(uRepository)
  val result = uService.getU(uName)
  assertEquals(result, uOption)
}
