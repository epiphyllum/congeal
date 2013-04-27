package congeal

import language.experimental.macros
import scala.reflect.macros.{ Context, Universe }

/** Contains the implementation for the `hasPart` type macro. */
private[congeal] object HasPartMacroImpl extends MacroImpl {

  // TODO: this code is duplicated in HasDependencyMacroImpl
  private type ImplClassName = String
  private var reverseLookup: Map[ImplClassName, Any] = Map()
  def baseClass(c: Context)(implClassName: String): c.Type =
    reverseLookup(implClassName).asInstanceOf[c.Type]

  override protected val macroName = "hasPart"

  override def classDef(c: Context)(t: c.Type, implClassName: c.TypeName): c.universe.ClassDef = {
    import c.universe._

    reverseLookup += (implClassName.toString -> t)

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
