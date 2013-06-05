package congeal

import language.experimental.macros
import scala.reflect.macros.Context
import scala.reflect.macros.Universe

private[congeal] object HasPrivatePartMacroImpl {

  def apply(c0: Context)(t0: c0.Type) = new HasPrivatePartMacroImpl {
    val c: c0.type = c0
    val t = t0
  }

}

/** Contains the implementation for the `hasPrivatePart` type macro. */
private[congeal] abstract class HasPrivatePartMacroImpl extends MacroImpl {

  import c.universe._

  override protected val macroName = "hasPrivatePart"

  override def classDef(implClassName: c.TypeName): ClassDef = {

    // trait hasPrivatePart[T]
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
