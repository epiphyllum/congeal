package congeal

import scala.reflect.macros.{ Context, Universe }

/** Ensures that the type in the macro context is "simple". Produces compiler
  * errors for any of the ways the type fails to be simple.
  */
private[congeal] trait EnsureSimpleType extends SymbolPredicates {
  self: MacroImpl =>

  import c.universe._

  protected val macroName: String

  protected def ensureSimpleType() {
    ensureTypeIsTrait()
    ensureTypeIsStatic()
    ensureNoNonPrivateThisInnerClasses()
    ensureNoSelfReferencingMembers()
  }

  private def ensureTypeIsTrait() {
    val ts = t.typeSymbol
    if (! (ts.isClass && ts.asClass.isTrait)) {
      c.error(c.enclosingPosition, s"${ts.name} must be a trait in $macroName[${ts.name}]")
    }
  }

  private def ensureTypeIsStatic() {
    if (!t.typeSymbol.isStatic) {
      // "static" means not a member of a method or trait. only objects all the way up
      val ts = t.typeSymbol
      c.error(c.enclosingPosition, s"${ts.name} must be static in $macroName[${ts.name}]")
    }
  }

  private def ensureNoNonPrivateThisInnerClasses() {
    val ts = t.typeSymbol
    val nonPrivateThisInnerClasses = t.members.filter { symbol =>
      symbol.isClass && ! (symbol.isPrivate && symbol.privateWithin == ts)
    }
    if (nonPrivateThisInnerClasses.nonEmpty) {
      c.error(c.enclosingPosition, s"${ts.name} must not have non-private[this] inner classes in $macroName[${ts.name}]")
    }
  }

  private def ensureNoSelfReferencingMembers() {
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
