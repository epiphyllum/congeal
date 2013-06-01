package congeal

import language.experimental.macros
import scala.reflect.macros.Context
import scala.reflect.macros.Universe

private[congeal] object HasPartMacroImpl {

  def apply(c0: Context)(t0: c0.Type) = new HasPartMacroImpl {
    val c: c0.type = c0
    val t = t0
  }

}

/** Contains the implementation for the `hasPart` type macro. */
private[congeal] abstract class HasPartMacroImpl extends MacroImpl {

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
