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

  import c.universe._

  override protected val macroName = "hasPart"

  override def classDef(implClassName: c.TypeName): ClassDef = {

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
