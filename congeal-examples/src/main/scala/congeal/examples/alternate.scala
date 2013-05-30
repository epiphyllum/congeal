package congeal.examples.alternate

import congeal._
import congeal.examples.flat._

trait AltURepository extends standsInFor[URepository] {
  override def getU(uName: String): Option[U] = None
}

trait AltRoot extends
  standsInFor[Root] with
  hasPart[SRepository] with
  hasPart[TRepository] with
  hasPart[AltURepository] with
  hasPart[SService] with
  hasPart[TService] with
  hasPart[UService]

/* outputs
Some(S(testSName))
Some(S(testSName))
Some(T(testTName))
Some(T(testTName))
None
None
*/
object ApplicationTest extends App {
  new Application with componentImpl[AltRoot]
}

// just override Root so i dont have to spell out all the parts
trait AltRoot2 extends
  standsInFor[Root] with
  Root with
  hasPart[AltURepository]

object ApplicationTest2 extends App {
  new Application with componentImpl[AltRoot2]
}
