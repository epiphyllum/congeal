package congeal

import language.experimental.macros
import scala.reflect.macros.{ Context, Universe }

/** Contains the implementation for the `hasDependency` type macro. */
private[congeal] object HasDependencyMacroImpl extends MacroImpl {

  override protected val macroName = "hasDependency"

  override def classDef(c: Context)(t: c.Type, implClassName: c.TypeName): c.universe.ClassDef = {
    import c.universe._

    // trait hasDependency[T] extends componentApi[T]
    ClassDef(
      Modifiers(Flag.ABSTRACT | Flag.INTERFACE | Flag.DEFAULTPARAM),
      implClassName,
      List(),
      Template(List(ComponentApiMacroImpl.refToTopLevelClassDef(c)(t)),
               emptyValDef,
               List()))
  }

}
