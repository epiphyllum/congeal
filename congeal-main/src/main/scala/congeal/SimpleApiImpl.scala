package congeal

import language.experimental.macros
import scala.reflect.macros.{ Context, Universe }

/** Contains the implementation for the `simpleApi` type macro. */
private[congeal] object SimpleApiImpl extends MacroImpl {

  /** Provides implementation of type macro `congeal.simpleApi`. */
  def simpleApiImpl[T: c.WeakTypeTag](c: Context): c.Tree = impl[T](c)

  /** Produces a tree for the simpleApi of the supplied type t. */
  def simpleApiTree(c: Context)(t: c.Type): c.Tree = implTree(c)(t)

  override protected val macroName = "simpleApi"

  override protected def createClassDef(c: Context)(t: c.Type, implClassName: c.TypeName): c.universe.ClassDef = {
    import c.universe._
    val internalSymbolTable = c.universe.asInstanceOf[scala.reflect.internal.SymbolTable]

    // FIX: should be members not declarations
    val body = t.declarations.filter(symbolIsNonConstructorMethod(c)(_)).map { s =>
      val method = s.asMethod

      val tparams = method.typeParams map { tp =>
        internalSymbolTable.TypeDef(tp.asInstanceOf[internalSymbolTable.Symbol]).asInstanceOf[c.universe.TypeDef]
      }
      val paramss = method.paramss map {
        _ map { p =>
          internalSymbolTable.ValDef(p.asInstanceOf[internalSymbolTable.Symbol]).asInstanceOf[c.universe.ValDef]
        }
      }

      // FIX: duplicated in SimpleImplImpl
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

      DefDef(
        Modifiers(Flag.DEFERRED), // FIX: match protected/public/whatever of copied method
        s.name,
        tparams,
        paramss,
        typeTree(method.returnType),
        EmptyTree)
    }

    ClassDef(Modifiers(Flag.ABSTRACT | Flag.TRAIT), implClassName, Nil, Template(
      List(Ident(TypeName("AnyRef"))),
      emptyValDef,
      body.toList))
  }
}
