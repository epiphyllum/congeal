package congeal

import language.experimental.macros
import scala.reflect.macros.{ Context, Universe }

/** Contains the implementation for the `simpleApi` type macro. */
private[congeal] object SimpleApiImpl extends EnsureSimpleType with SymbolPredicates {

  // TODO: fix problem of passing around contexts and importing universe

  private type BaseClassId = String // use the fullName for now
  private type SimpleApiClassName = String
  private var simpleApiCache: Map[BaseClassId, SimpleApiClassName] = Map()

  def simpleApiImpl[T: c.WeakTypeTag](c: Context): c.Tree = {
    import c.universe._

    val t: Type = weakTypeOf[T]
    ensureSimpleType(c)(t, "simpleApi")
    if (c.hasErrors) {
      Ident(definitions.AnyRefClass)
    }
    else {
      simpleApiTree(c)(t)
    }
  }

  /** Produces a tree for the simpleApi of the supplied type t. */
  def simpleApiTree(c: Context)(t: c.Type): c.Tree = {
    import c.universe._
    val hiddenPackage = Select(Ident(TermName("congeal")), TermName("hidden"))
    val className = createOrLookupSimpleApi(c)(t)
    Select(hiddenPackage, TypeName(className))
  }

  private def createOrLookupSimpleApi(c: Context)(t: c.Type): SimpleApiClassName = {
    import c.universe._
    val ts: TypeSymbol = t.typeSymbol.asType
    val typeFullName = ts.fullName
    simpleApiCache.getOrElse(typeFullName, createSimpleApi(c)(t))
  }

  private def createSimpleApi(c: Context)(t: c.Type): SimpleApiClassName = {
    import c.universe._
    val internalSymbolTable = c.universe.asInstanceOf[scala.reflect.internal.SymbolTable]

    val ts: TypeSymbol = t.typeSymbol.asType
    val typeFullName = ts.fullName
    val hiddenPackage = Select(Ident(TermName("congeal")), TermName("hidden"))
    val packageName = hiddenPackage.toString
    val className = c.freshName(ts.name).toTypeName

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

      val dd = DefDef(
        Modifiers(Flag.DEFERRED), // FIX: match protected/public/whatever of copied method
        s.name,
        tparams,
        paramss,
        typeTree(method.returnType),
        EmptyTree)
      dd
    }

    val clazz = ClassDef(Modifiers(Flag.ABSTRACT | Flag.TRAIT), className, Nil, Template(
      List(Ident(TypeName("AnyRef"))),
      emptyValDef,
      body.toList))
    c.introduceTopLevel(packageName, clazz)
    simpleApiCache += (typeFullName -> className.toString)
    className.toString
  }
}
