package congeal

import language.experimental.macros
import scala.reflect.macros.Context
import scala.reflect.macros.Universe

/** Contains the implementation for the `standsInFor` type macro. */
private[congeal] object StandsInForMacroImpl extends MacroImpl {

  override protected val macroName = "standsInFor"

  override def classDef(c: Context)(t: c.Type, implClassName: c.TypeName): c.universe.ClassDef = {
    import c.universe._

    // trait standsInFor[T] extends api[T]
    ClassDef(
      Modifiers(Flag.ABSTRACT | Flag.INTERFACE | Flag.DEFAULTPARAM),
      implClassName,
      List(),
      Template(
        List(ApiMacroImpl.refToTopLevelClassDef(c)(t)),
        emptyValDef,
        List()))
  }

}
