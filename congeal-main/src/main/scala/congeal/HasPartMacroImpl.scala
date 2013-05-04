package congeal

import language.experimental.macros
import scala.reflect.macros.{ Context, Universe }

/** Contains the implementation for the `hasPart` type macro. */
private[congeal] object HasPartMacroImpl extends MacroImpl {

  override protected val macroName = "hasPart"

  override def classDef(c: Context)(t: c.Type, implClassName: c.TypeName): c.universe.ClassDef = {
    import c.universe._

    // trait hasPart[T]
    ClassDef(
      Modifiers(Flag.ABSTRACT | Flag.INTERFACE | Flag.DEFAULTPARAM),
      implClassName,
      List(),
      Template(
        List(Ident(TypeName("AnyRef"))),
        emptyValDef,
        List()))
  }

}
