package congeal.examples.uimodel

import congeal._

case class U(uName: String)

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

trait UiView extends hasDependency[UiReadOnlyModel] {
  def init: Unit = println("hi from initView")
}

trait UiController extends
  hasDependency[UiWriteOnlyModel] with
  hasDependency[UiView] {
  def init: Unit = println("hi from initController")
}

trait Root extends
  hasPart[UiModel] with
  hasPart[UiView] with
  hasPart[UiController]

trait Application extends componentApi[Root] {
  println(uiReadOnlyModel.getUOption)
  uiWriteOnlyModel.setUOption(Some(U("uName")))
  println(uiReadOnlyModel.getUOption)
}

/* outputs
None
Some(U(uName))
*/
object ApplicationTest extends App {
  new Application with componentImpl[Root]
}
