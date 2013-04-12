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

    // FIX: below is schlock

    val ts: TypeSymbol = t.typeSymbol.asType

    // FIX: this gives package name of the macro user. use package of T instead
    val packageName = c.enclosingPackage.pid.toString
    println(s"packageName = $packageName")

    // FIX: this gives class name based on the name of the class where the macro is used. use a variation on the name of type T instead
    val className = c.freshName(c.enclosingImpl.name).toTypeName
    println(s"className = $className")

    // FIX: in typeT I have a Type
    // what i need is a Template
    // Templates have: (parents: List[Universe.Tree], self: Universe.ValDef, body: List[Universe.Tree])

    val Expr(Block(List(ClassDef(_, _, _, Template(parents, self, body))), _)) = reify {
      class CONTAINER {
        def bar = println("hi from the other side")
      }
    }

    println(s"parents = $parents")
    println(s"self = $self")

    val clazz = ClassDef(NoMods, className, Nil, Template(parents, self, body))

    c.introduceTopLevel(packageName, clazz)
    val classRef = Select(c.enclosingPackage.pid, className)
    classRef
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
