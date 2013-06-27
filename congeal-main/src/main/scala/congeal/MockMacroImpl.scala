package congeal

import language.experimental.macros
import scala.reflect.macros.Context
import scala.reflect.macros.Universe

private[congeal] object MockMacroImpl {

  def apply(c0: Context)(t0: c0.Type)(mocker: c0.Expr[Function1[Class[_], _]]) = new MockMacroImpl {
    val c: c0.type = c0
    val t = t0
    override protected val macroName = s"mock_${mocker.hashCode}"
  }

}

/** Contains the implementation for the `standsInFor` type macro. */
private[congeal] abstract class MockMacroImpl extends MacroImpl {

  import c.universe._

  override def classDef(implClassName: c.TypeName): ClassDef = {

    // trait easyMock[T] extends api[T]
    ClassDef(
      Modifiers(Flag.ABSTRACT | Flag.INTERFACE | Flag.DEFAULTPARAM),
      implClassName,
      List(),
      Template(
        List(ApiMacroImpl(c)(t).refToTopLevelClassDef),
        emptyValDef,
        List()))
  }

}
