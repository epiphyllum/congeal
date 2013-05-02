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
    if (!topLevelClassDefIsDefined(c)(t)) {
      introduceTopLevelClassDef(c)(t)
    }
    val parts = fullNameParts(c)(t)
    val packageParts = parts.dropRight(1)
    val className = parts.last
    if (packageParts.isEmpty) {
      Ident(TypeName(className))
    }
    else {
      def outerPackage(packageParts: List[String]): c.Tree = {
        if (packageParts.size == 1) {
          Ident(TermName(packageParts.head))
        }
        else {
          Select(
            outerPackage(packageParts.dropRight(1)),
            TermName(packageParts.last))
        }
      }
      Select(outerPackage(packageParts), TypeName(className))
    }
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
      case ClassInfoType(_, _, typeSymbol) =>
        typeTree(c)(typeSymbol.asType.toType)
    }
  }

  private def topLevelClassDefIsDefined(c: Context)(t: c.Type): Boolean = {
    // basic idea here is if i dont get an exception retrieving the type then it is defined
    import c.universe._
    val parts = fullNameParts(c)(t)
    try {
      if (parts.size == 1) {
        c.mirror.staticClass(parts(0))
        true
      }
      else {
        val outermostPackage = c.mirror.staticPackage(parts.head)
        def defined(outerPackage: ModuleSymbol, parts: List[String]): Boolean = {
          if (parts.size == 1) {
            outerPackage.moduleClass.typeSignature.member(TypeName(parts.head)) != NoSymbol
          }
          else {
            defined(
              outerPackage.moduleClass.typeSignature.member(TermName(parts.head)).asModule,
              parts.tail)
          }
        }
        defined(outermostPackage, parts.tail)
      }
    }
    catch {
      case x: scala.reflect.internal.MissingRequirementError => return false
      case x: scala.ScalaReflectionException => return false
    }
  }

  private def fullNameParts(c: Context)(t: c.Type): List[String] =
    "congeal" :: "hidden" :: macroName :: t.typeSymbol.fullName.split('.').toList

  private def introduceTopLevelClassDef(c: Context)(t: c.Type) {
    import c.universe._
    val parts = fullNameParts(c)(t)
    val packageName = parts.dropRight(1).mkString(".")
    val className = parts.last
    val clazz = classDef(c)(t, TypeName(className).toTypeName)
    c.introduceTopLevel(packageName, clazz)
  }

}
