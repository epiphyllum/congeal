package congeal

import language.experimental.macros
import scala.reflect.macros.{ Context, Universe }

/** Contains helper methods shared by `componentApi` and `componentImpl` type macros. */
private[congeal] trait UnderlyingTypesOfSupers extends StaticSymbolLookup {

  /** List of the underlying types of all the supers of the supplied type that are `hasPart` implementations. */
  protected def underlyingTypesOfHasPartSupers(c: Context)(t: c.Type): List[c.Type] =
    underlyingTypesOfSupers(c)(t, "hasPart")

  /** List of the underlying types of all the supers of the supplied type that are `hasDependency` implementations. */
  protected def underlyingTypesOfHasDependencySupers(c: Context)(t: c.Type): List[c.Type] =
    underlyingTypesOfSupers(c)(t, "hasDependency")

  private def underlyingTypesOfSupers(c: Context)(t: c.Type, macroName: String): List[c.Type] = {
    val macroTypeName = t.typeSymbol.fullName
    val macroPrefix = s"congeal.hidden.$macroName."
    def tail = t.baseClasses.tail flatMap { s => underlyingTypesOfSupers(c)(s.typeSignature, macroName) }
    if (macroTypeName.startsWith(macroPrefix)) {
      val underlyingTypeName = macroTypeName.substring(macroPrefix.size)
      val underlyingType = staticSymbol(c)(underlyingTypeName).typeSignature
      underlyingType :: tail
    }
    else
      tail
  }

}
