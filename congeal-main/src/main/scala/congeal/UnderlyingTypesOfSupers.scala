package congeal

import language.experimental.macros
import scala.reflect.macros.Context
import scala.reflect.macros.Universe

/** Contains helper methods shared by `componentApi` and `componentImpl` type macros. */
private[congeal] trait UnderlyingTypesOfSupers extends StaticSymbolLookup {

  /** List of the underlying types of all the supers of the supplied type that are `hasPart` implementations. */
  protected def underlyingTypesOfHasPartSupers(c: Context)(t: c.Type): List[c.Type] =
    underlyingTypesOfSupers(c)(t, "hasPart")

  /** List of the underlying types of all the supers of the supplied type that are `hasDependency` implementations. */
  protected def underlyingTypesOfHasDependencySupers(c: Context)(t: c.Type): List[c.Type] =
    underlyingTypesOfSupers(c)(t, "hasDependency")

  /** List of the underlying types of all the supers of the supplied type that are `standsInFor` implementations. */
  protected def underlyingTypesOfStandsInForSupers(c: Context)(t: c.Type): List[c.Type] =
    underlyingTypesOfSupers(c)(t, "standsInFor")

  /** List of the underlying types of all the supers of the supplied type that are `easyMock` implementations. */
  protected def underlyingTypesOfEasyMockSupers(c: Context)(t: c.Type): List[c.Type] =
    underlyingTypesOfSupers(c)(t, "easyMock")

  private def underlyingTypesOfSupers(c: Context)(t: c.Type, macroName: String): List[c.Type] = {
    def tfn(t: c.Type): String = t.typeSymbol.fullName
    //println(s"underlyingTypesOfSupers $macroName ${tfn(t)} bases = ${t.baseClasses map {x=>x.fullName}}")

    val macroPrefix = s"congeal.hidden.$macroName."
    t.baseClasses flatMap { typeSymbol =>
      val macroTypeName = typeSymbol.fullName
      if (macroTypeName.startsWith(macroPrefix)) {
        val underlyingTypeName = macroTypeName.substring(macroPrefix.size)
        val underlyingType = staticSymbol(c)(underlyingTypeName).typeSignature
        Some(underlyingType)
      }
      else
        None
    }
  }

}
