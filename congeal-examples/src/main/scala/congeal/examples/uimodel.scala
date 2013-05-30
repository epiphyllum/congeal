package congeal.examples.uimodel

import congeal._
import congeal.examples.flat._

trait UiReadOnlyModel {
  def getUOption: Option[U]
}

trait UiWriteOnlyModel {
  def setUOption(uOption: Option[U]): Unit
}

trait UiModel extends
  standsInFor[UiReadOnlyModel] with
  standsInFor[UiWriteOnlyModel] {
  private var uOption: Option[U] = None
  override def getUOption = uOption
  override def setUOption(uOption: Option[U]) {
    this.uOption = uOption
  }
}

trait Root extends
  hasPart[UiModel]

trait Application extends componentApi[Root] {
  println(uiReadOnlyModel.getUOption)
  uiWriteOnlyModel.setUOption(Some(U("uName")))
  println(uiReadOnlyModel.getUOption)
}

/* outputs
None
Some(T(testTName))
*/
object ApplicationTest extends App {
  new Application with componentImpl[Root]
}
