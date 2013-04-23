package congeal

import language.experimental.macros
import scala.reflect.macros.{ Context, Universe }

/** Contains the implementation for the `simpleImpl` type macro. */
// FIX: if there is nothing public in the package outside the package object, then i do not get scaladoc for the package object
//private[congeal]
object SimpleImplImpl extends EnsureSimpleType with SymbolPredicates {

  // TODO: fix problem of passing around contexts and importing universe

  // FIX: i had a name clash between this BaseClassId and the one in SimpleApi. resolve better
  private type ImplBaseClassId = String // use the fullName for now
  private type SimpleImplClassName = String
  private var simpleImplCache: Map[ImplBaseClassId, SimpleImplClassName] = Map()

  def simpleImplImpl[T: c.WeakTypeTag](c: Context): c.Tree = {
    import c.universe._
    val t: Type = weakTypeOf[T]
    ensureSimpleType(c)(t, "simpleImpl")
    if (c.hasErrors) {
      Ident(definitions.AnyRefClass)
    }
    else {
      simpleImplTree(c)(t)
    }
  }

  /** Produces a tree for the simpleImpl of the supplied type t. */
  protected def simpleImplTree(c: Context)(t: c.Type): c.Tree = {
    import c.universe._
    val hiddenPackage = Select(Ident(TermName("congeal")), TermName("hidden"))
    val className = createOrLookupSimpleImpl(c)(t)
    Select(hiddenPackage, TypeName(className))
  }

  private def createOrLookupSimpleImpl(c: Context)(t: c.Type): SimpleImplClassName = {
    import c.universe._
    val ts: TypeSymbol = t.typeSymbol.asType
    val typeFullName = ts.fullName
    simpleImplCache.getOrElse(typeFullName, createSimpleImpl(c)(t))
  }

  private def createSimpleImpl(c: Context)(t: c.Type): SimpleImplClassName = {
    import c.universe._
    val internalSymbolTable = c.universe.asInstanceOf[scala.reflect.internal.SymbolTable]

    val ts: TypeSymbol = t.typeSymbol.asType
    val typeFullName = ts.fullName
    val hiddenPackage = Select(Ident(TermName("congeal")), TermName("hidden"))
    val packageName = hiddenPackage.toString
    val className = c.freshName(ts.name).toTypeName

    // FIX: duplicated in SimpleApiImpl
    def typeTree(t: Type): Tree = {
      t match {
        case TypeRef(pre, sym, args) if args.isEmpty =>
          Select(Ident(pre.termSymbol), sym.name)
        case TypeRef(pre, sym, args) if args.nonEmpty =>
          AppliedTypeTree(
            typeTree(TypeRef(pre, sym, Nil)),
            args map { a => typeTree(a) })
      }
    }
    
    val clazz =
      ClassDef(Modifiers(), className, List(), Template(
        List(
          typeTree(t),
          SimpleApiImpl.simpleApiTree(c)(t)),
        emptyValDef,
        List(DefDef(Modifiers(), nme.CONSTRUCTOR, List(), List(List()), TypeTree(),
                    Block(List(Apply(Select(Super(This(tpnme.EMPTY), tpnme.EMPTY), nme.CONSTRUCTOR), List())), Literal(Constant(())))))))

    c.introduceTopLevel(packageName, clazz)
    simpleImplCache += (typeFullName -> className.toString)
    className.toString
  }
}
