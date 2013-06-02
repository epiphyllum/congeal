package congeal

import scala.reflect.macros.Context
import scala.reflect.macros.Universe

/** An implementation for a type macro that takes a single type parameter as argument. Ensures the
  * provided type is a simple type, and if so, generates a hidden `ClassDef` for the macro result,
  * and returns a `Tree` that references the hidden class. Caches references to the `ClassDef`s as
  * they are built, so that only a single class is produced per input type.
  * 
  * Implementing classes must provide the `macroName`, for error reporting, and a method for
  * creating a `ClassDef` for an input `Type`.
  */
private[congeal] abstract class MacroImpl extends EnsureSimpleType with StaticSymbolLookup {

  protected val c: Context
  protected val t: c.Type

  import c.universe._

  /** Produces a tree referencing a hidden, top-level `ClassDef` for the macro result. Ensures that
    * the type represented by the provided type parameter is a simple type.
    */
  def refToTopLevelClassDefEnsureSimple: c.Tree = {
    ensureSimpleType(c)(t)
    if (c.hasErrors) {
      Ident(definitions.AnyRefClass)
    }
    else {
      refToTopLevelClassDef
    }
  }

  /** Produces a tree referencing a hidden, top-level `ClassDef` for the macro result. May assume
    * that the type represented by the provided type parameter is a simple type.
    */
  def refToTopLevelClassDef: c.Tree = {
    if (!topLevelClassDefIsDefined) {
      introduceTopLevelClassDef
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
  def classDef(implClassName: c.TypeName): ClassDef

  /** The name of the macro. for error reporting. */
  protected val macroName: String

  // TODO: consider move into a supporting traits
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

  private def topLevelClassDefIsDefined =
    staticSymbol(c)(macroClassName(c)(t)) != c.universe.NoSymbol

  private def macroClassName(c: Context)(t: c.Type) =
    "congeal.hidden." + macroName + "." + t.typeSymbol.fullName

  private def introduceTopLevelClassDef {
    //println(s"introduceTopLevelClassDef ${this.getClass} ${t.typeSymbol.fullName}")
    import c.universe._
    val parts = macroClassName(c)(t).split('.').toList
    val packageName = parts.dropRight(1).mkString(".")
    val className = parts.last
    val clazz = classDef(TypeName(className).toTypeName)
    c.introduceTopLevel(packageName, clazz)
  }

  private def fullNameParts(c: Context)(t: c.Type): List[String] =
    macroClassName(c)(t).split('.').toList

}
