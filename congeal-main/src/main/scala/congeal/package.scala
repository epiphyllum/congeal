import language.experimental.macros
import scala.reflect.macros.Context

/** Contains implementations of the congeal type macros. */
package object congeal {

  /** Produces an API for the supplied type `A`.
    * 
    * Requires type `A` to be "simple", i.e., meet the following conditions:
    * 
    *   - is a trait
    *   - no non-private[this] inner classes
    *   - no members that have params or return types that derive from A
    *
    * @tparam A the base type to produce an API for
    */
  type simpleApi[A] = macro simpleApiImpl[A]

  def simpleApiImpl[T: c.WeakTypeTag](c: Context): c.Tree = {
    import c.universe._

    val t: Type = weakTypeOf[T]

    ensureTypeIsTrait(c)(t)
    ensureNoNonPrivateThisInnerClasses(c)(t)
    ensureNoSelfReferencingMembers(c)(t)

    val ts: TypeSymbol = t.typeSymbol.asType
    val hiddenPackage = Select(Ident(TermName("congeal")), TermName("hidden"))
    val packageName = hiddenPackage.toString
    val className = c.freshName(ts.name).toTypeName

    val body =
      List(
        DefDef(
          Modifiers(), nme.CONSTRUCTOR, List(), List(List()), TypeTree(),
          Block(List(Apply(Select(Super(This(tpnme.EMPTY), tpnme.EMPTY), nme.CONSTRUCTOR), List())), Literal(Constant(())))),
        DefDef(
          Modifiers(), TermName("bar"), List(), List(), TypeTree(),
          Apply(Select(Ident(definitions.PredefModule), TermName("println")), List(Literal(Constant("hi from the other side"))))))

    val clazz = ClassDef(NoMods, className, Nil, Template(
      List(Ident(TypeName("AnyRef"))),
      emptyValDef,
      body))
    c.introduceTopLevel(packageName, clazz)
    Select(hiddenPackage, className)
  }

  private def ensureTypeIsTrait(c: Context)(t: c.Type) {
    val ts = t.typeSymbol
    if (! (ts.isClass && ts.asClass.isTrait)) {
      c.error(c.enclosingPosition, s"${ts.name} must be a trait in simpleApi[${ts.name}]")
    }
  }

  private def ensureNoNonPrivateThisInnerClasses(c: Context)(t: c.Type) {
    val ts = t.typeSymbol
    val nonPrivateThisInnerClasses = t.members.filter { symbol =>
      symbol.isClass && ! (symbol.isPrivate && symbol.privateWithin == ts)
    }
    if (nonPrivateThisInnerClasses.nonEmpty) {
      c.error(c.enclosingPosition, s"${ts.name} must not have non-private[this] inner classes in simpleApi[${ts.name}]")
    }
  }

  private def ensureNoSelfReferencingMembers(c: Context)(t: c.Type) {
    val ts = t.typeSymbol
    val selfReferencingMembers = t.members.filter { symbol =>
      (symbol.isMethod || (symbol.isTerm && (symbol.asTerm.isVal || symbol.asTerm.isVar))) &&
      ! (symbol.isPrivate && symbol.privateWithin == ts) &&
      symbol.typeSignature.find(_ == t).nonEmpty
    }
    if (selfReferencingMembers.nonEmpty) {
      c.error(c.enclosingPosition, s"${ts.name} must not have self-referencing members in simpleApi[${ts.name}]")
    }
  }

}
