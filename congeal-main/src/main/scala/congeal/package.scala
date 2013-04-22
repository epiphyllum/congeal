import language.experimental.macros
import scala.reflect.macros.{ Context, Universe }

/** Contains implementations of the congeal type macros. */
package object congeal {

  // TODO: fix problem of passing around contexts and importing universe

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

  type BaseClassId = String // use the fullName for now
  type SimpleApiClassName = String
  private var simpleApiCache: Map[BaseClassId, SimpleApiClassName] = Map()

  def simpleApiImpl[T: c.WeakTypeTag](c: Context): c.Tree = {
    import c.universe._

    val t: Type = weakTypeOf[T]

    ensureTypeIsClassOrTrait(c)(t)
    ensureNoNonPrivateThisInnerClasses(c)(t)
    ensureNoSelfReferencingMembers(c)(t)

    if (c.hasErrors) {
      Ident(definitions.AnyRefClass)
    }
    else {
      val hiddenPackage = Select(Ident(TermName("congeal")), TermName("hidden"))
      val className = createOrLookupSimpleApi(c)(t)
      Select(hiddenPackage, TypeName(className))
    }
  }

  private def ensureTypeIsClassOrTrait(c: Context)(t: c.Type) {
    val ts = t.typeSymbol
    if (!ts.isClass) {
      c.error(c.enclosingPosition, s"${ts.name} must be a class or trait in simpleApi[${ts.name}]")
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
      c.error(c.enclosingPosition, s"${ts.name} must not have self-referencing members in simpleApi[${ts.name}]")
    }
  }

  private def symbolIsNonConstructorMethod(c: Context)(s: c.Symbol) = s.isMethod && !s.asMethod.isConstructor

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
