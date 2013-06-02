package congeal

import language.experimental.macros
import scala.reflect.macros.Context
import scala.reflect.macros.Universe

private[congeal] object HasDependencyMacroImpl {

  def apply(c0: Context)(t0: c0.Type) = new HasDependencyMacroImpl {
    val c: c0.type = c0
    val t = t0
  }

}

/** Contains the implementation for the `hasDependency` type macro. */
private[congeal] abstract class HasDependencyMacroImpl extends MacroImpl {

  override protected val macroName = "hasDependency"

  override def classDef(c: Context)(t: c.Type, implClassName: c.TypeName): c.universe.ClassDef = {
    import c.universe._

    // trait hasDependency[T] extends componentApi[T]
    ClassDef(
      Modifiers(Flag.ABSTRACT | Flag.INTERFACE | Flag.DEFAULTPARAM),
      implClassName,
      List(),
      Template(List(ComponentApiMacroImpl(c)(t).refToTopLevelClassDef),
               emptyValDef,
               List()))
  }

}
