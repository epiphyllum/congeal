package congeal

import scala.reflect.macros.{ Context, Universe }

/** Ensures that the type in the macro context is "simple", i.e., meets the
  * following conditions:
  * 
  *   - is a trait
  *   - is static (i.e., not a member of a method or trait. only objects all the way up.)
  *   - no non-private[this] inner classes
  *   - no members that have params or return types that derive from A
  */
trait EnsureSimpleType extends SymbolPredicates {

  protected def ensureSimpleType(c: Context)(t: c.Type, macroName: String) {
    ensureTypeIsTrait(c)(t, macroName)
    ensureTypeIsStatic(c)(t, macroName)
    ensureNoNonPrivateThisInnerClasses(c)(t, macroName)
    ensureNoSelfReferencingMembers(c)(t, macroName)
  }

  private def ensureTypeIsTrait(c: Context)(t: c.Type, macroName: String) {
    val ts = t.typeSymbol
    if (! (ts.isClass && ts.asClass.isTrait)) {
      c.error(c.enclosingPosition, s"${ts.name} must be a trait in $macroName[${ts.name}]")
    }
  }

  private def ensureTypeIsStatic(c: Context)(t: c.Type, macroName: String) {
    if (!t.typeSymbol.isStatic) {
      // "static" means not a member of a method or trait. only objects all the way up
      val ts = t.typeSymbol
      c.error(c.enclosingPosition, s"${ts.name} must be static in $macroName[${ts.name}]")
    }
  }

  private def ensureNoNonPrivateThisInnerClasses(c: Context)(t: c.Type, macroName: String) {
    val ts = t.typeSymbol
    val nonPrivateThisInnerClasses = t.members.filter { symbol =>
      symbol.isClass && ! (symbol.isPrivate && symbol.privateWithin == ts)
    }
    if (nonPrivateThisInnerClasses.nonEmpty) {
      c.error(c.enclosingPosition, s"${ts.name} must not have non-private[this] inner classes in $macroName[${ts.name}]")
    }
  }

  private def ensureNoSelfReferencingMembers(c: Context)(t: c.Type, macroName: String) {
    import c.universe._
    val ts = t.typeSymbol
    def symbolIsValOrVar(s: Symbol) = s.isTerm && (s.asTerm.isVal || s.asTerm.isVar)
    def symbolIsPrivateThis(s: Symbol) = s.isPrivate && s.privateWithin == ts
    def symbolHasTypeInSignature(s: Symbol) = s.typeSignature.find(_ == t).nonEmpty
    val selfReferencingMembers = t.members.filter { s =>
      (symbolIsNonConstructorMethod(c)(s) || symbolIsValOrVar(s)) &&
      !symbolIsPrivateThis(s) &&
      symbolHasTypeInSignature(s)
    }
    if (selfReferencingMembers.nonEmpty) {
      c.error(c.enclosingPosition, s"${ts.name} must not have self-referencing members in $macroName[${ts.name}]")
    }
  }

}
