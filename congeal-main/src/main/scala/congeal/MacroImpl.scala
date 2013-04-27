package congeal

import scala.reflect.macros.{ Context, Universe }

/** An implementation for a type macro that takes a single type parameter as argument. Ensures the
  * provided type is a simple type, and if so, generates a hidden `ClassDef` for the macro result,
  * and returns a `Tree` that references the hidden class. Caches references to the `ClassDef`s as
  * they are built, so that only a single class is produced per input type.
  * 
  * Implementing classes must provide the `macroName`, for error reporting, and a method for
  * creating a `ClassDef` for an input `Type`.
  */
private[congeal] trait MacroImpl extends EnsureSimpleType {

  private type BaseClassId = String // use the fullName for now
  private type ImplClassName = String
  private var implCache: Map[BaseClassId, ImplClassName] = Map()

  /** Produces a tree referencing a hidden, top-level `ClassDef` for the macro result. Ensures that
    * the type represented by the provided type parameter is a simple type.
    */
  def refToTopLevelClassDefEnsureSimple[T: c.WeakTypeTag](c: Context): c.Tree = {
    import c.universe._
    val t: Type = weakTypeOf[T]
    ensureSimpleType(c)(t)
    if (c.hasErrors) {
      Ident(definitions.AnyRefClass)
    }
    else {
      refToTopLevelClassDef(c)(t)
    }
  }

  /** Produces a tree referencing a hidden, top-level `ClassDef` for the macro result. May assume
    * that the type represented by the provided type parameter is a simple type.
    */
  def refToTopLevelClassDef(c: Context)(t: c.Type): c.Tree = {
    import c.universe._
    val className = createOrLookupImpl(c)(t)
    val hiddenPackage = Select(Ident(TermName("congeal")), TermName("hidden"))
    Select(hiddenPackage, TypeName(className))
  }

  /** A `ClassDef` to represent the macro result for the supplied input type. */
  def classDef(c: Context)(t: c.Type, implClassName: c.TypeName): c.universe.ClassDef

  /** The name of the macro. for error reporting. */
  protected val macroName: String

  protected def typeTree(c: Context)(t: c.Type): c.Tree = {
    import c.universe._
    t match {
      case TypeRef(pre, sym, args) if args.isEmpty =>
        Select(Ident(pre.termSymbol), sym.name)
      case TypeRef(pre, sym, args) if args.nonEmpty =>
        AppliedTypeTree(
          typeTree(c)(TypeRef(pre, sym, Nil)),
          args map { a => typeTree(c)(a) })
    }
  }

  private def createOrLookupImpl(c: Context)(t: c.Type): ImplClassName = {
    import c.universe._
    val ts: TypeSymbol = t.typeSymbol.asType
    val baseClassId = ts.fullName
    if (implCache.contains(baseClassId)) {
      implCache(baseClassId)
    }
    else {
      val implClassName = TypeName(macroName + "Of" + ts.name).toTypeName
      val clazz = classDef(c)(t, implClassName)
      val hiddenPackage = Select(Ident(TermName("congeal")), TermName("hidden"))
      c.introduceTopLevel(hiddenPackage.toString, clazz)
      implCache += (baseClassId -> implClassName.toString)
      implClassName.toString
    }
  }

}
