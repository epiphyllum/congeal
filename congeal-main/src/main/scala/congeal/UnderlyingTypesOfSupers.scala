package congeal

import language.experimental.macros
import scala.reflect.macros.Context
import scala.reflect.macros.Universe

/** Contains helper methods shared by `componentApi` and `componentImpl` type macros. */
private[congeal] trait UnderlyingTypesOfSupers extends StaticSymbolLookup {
  self: MacroImpl =>

  /** List of the underlying types of all the supers of the supplied type that are `hasPart` implementations. */
  protected def underlyingTypesOfHasPartSupers(t: c.Type): List[c.Type] =
    underlyingTypesOfSupers(t, "hasPart")

  /** List of the underlying types of all the supers of the supplied type that are `hasDependency` implementations. */
  protected def underlyingTypesOfHasDependencySupers(t: c.Type): List[c.Type] =
    underlyingTypesOfSupers(t, "hasDependency")

  /** List of the underlying types of all the supers of the supplied type that are `standsInFor` implementations. */
  protected def underlyingTypesOfStandsInForSupers(t: c.Type): List[c.Type] =
    underlyingTypesOfSupers(t, "standsInFor")

  /** List of the underlying types of all the supers of the supplied type that are `easyMock` implementations. */
  protected def underlyingTypesOfEasyMockSupers(t: c.Type): List[c.Type] =
    underlyingTypesOfSupers(t, "easyMock")

  private def underlyingTypesOfSupers(t: c.Type, macroName: String): List[c.Type] = {
    val macroPrefix = s"congeal.hidden.$macroName."
    t.baseClasses flatMap { typeSymbol =>
      val macroTypeName = typeSymbol.fullName
      if (macroTypeName.startsWith(macroPrefix)) {
        val underlyingTypeName = macroTypeName.substring(macroPrefix.size)
        val underlyingType = staticSymbol(underlyingTypeName).typeSignature
        Some(underlyingType)
      }
      else
        None
    }
  }

}
