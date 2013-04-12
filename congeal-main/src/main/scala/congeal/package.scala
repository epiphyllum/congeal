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

    val typeT: Type = weakTypeOf[T]
    println("TYPE " + typeT)

    val typeSymbolT: TypeSymbol = typeT.typeSymbol.asType
    if (! (typeSymbolT.isClass && typeSymbolT.asClass.isTrait)) {
      c.error(c.enclosingPosition, "simpleApi[A] only works if A is a trait")
    }

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

}
