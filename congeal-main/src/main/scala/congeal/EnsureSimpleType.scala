package congeal

import scala.reflect.macros.{ Context, Universe }

/** Ensures that the type in the macro context is "simple". Produces compiler
  * errors for any of the ways the type fails to be simple.
  */
private[congeal] trait EnsureSimpleType extends SymbolPredicates {

  protected val macroName: String

  protected def ensureSimpleType(c: Context)(t: c.Type) {
    ensureTypeIsTrait(c)(t)
    ensureTypeIsStatic(c)(t)
    ensureNoNonPrivateThisInnerClasses(c)(t)
    ensureNoSelfReferencingMembers(c)(t)
  }

  private def ensureTypeIsTrait(c: Context)(t: c.Type) {
    val ts = t.typeSymbol
    if (! (ts.isClass && ts.asClass.isTrait)) {
      c.error(c.enclosingPosition, s"${ts.name} must be a trait in $macroName[${ts.name}]")
    }
  }

  private def ensureTypeIsStatic(c: Context)(t: c.Type) {
    if (!t.typeSymbol.isStatic) {
      // "static" means not a member of a method or trait. only objects all the way up
      val ts = t.typeSymbol
      c.error(c.enclosingPosition, s"${ts.name} must be static in $macroName[${ts.name}]")
    }
  }

  private def ensureNoNonPrivateThisInnerClasses(c: Context)(t: c.Type) {
    val ts = t.typeSymbol
    val nonPrivateThisInnerClasses = t.members.filter { symbol =>
      symbol.isClass && ! (symbol.isPrivate && symbol.privateWithin == ts)
    }
    if (nonPrivateThisInnerClasses.nonEmpty) {
      c.error(c.enclosingPosition, s"${ts.name} must not have non-private[this] inner classes in $macroName[${ts.name}]")
    }
  }

  private def ensureNoSelfReferencingMembers(c: Context)(t: c.Type) {
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
