package congeal

import language.experimental.macros
import scala.reflect.macros.Context
import scala.reflect.macros.Universe

private[congeal] object EasyMockMacroImpl {

  def apply(c0: Context)(t0: c0.Type) = new EasyMockMacroImpl {
    val c: c0.type = c0
    val t = t0
  }

}

/** Contains the implementation for the `standsInFor` type macro. */
private[congeal] abstract class EasyMockMacroImpl extends MacroImpl {

  override protected val macroName = "easyMock"

  override def classDef(c: Context)(t: c.Type, implClassName: c.TypeName): c.universe.ClassDef = {
    import c.universe._

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
