package congeal

import language.experimental.macros
import scala.reflect.macros.{ Context, Universe }

/** Contains the implementation for the `simpleImpl` type macro. */
private[congeal] object SimpleImplImpl extends MacroImpl {

  /** Provides implementation of type macro `congeal.simpleImpl`. */
  def simpleImplImpl[T: c.WeakTypeTag](c: Context): c.Tree = impl[T](c)

  /** Produces a tree for the simpleImpl of the supplied type t. */
  protected def simpleImplTree(c: Context)(t: c.Type): c.Tree = implTree(c)(t)

  override protected val macroName = "simpleImpl"

  override protected def createClassDef(c: Context)(t: c.Type, implClassName: c.TypeName): c.universe.ClassDef = {
    import c.universe._

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

    ClassDef(Modifiers(), implClassName, List(), Template(
      List(
        typeTree(t),
        SimpleApiImpl.simpleApiTree(c)(t)),
      emptyValDef,
      List(DefDef(Modifiers(), nme.CONSTRUCTOR, List(), List(List()), TypeTree(),
                  Block(List(Apply(Select(Super(This(tpnme.EMPTY), tpnme.EMPTY), nme.CONSTRUCTOR), List())), Literal(Constant(())))))))
  }

}
