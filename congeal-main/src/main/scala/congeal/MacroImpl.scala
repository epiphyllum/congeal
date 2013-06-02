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
    ensureSimpleType()
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
    val parts = fullNameParts
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

  // TODO: consider moving typeTree into a supporting trait
  protected def typeTree(t: c.Type): c.Tree = t match {
    case TypeRef(pre, sym, args) if args.isEmpty =>
      Select(Ident(pre.termSymbol), sym.name)
    case TypeRef(pre, sym, args) if args.nonEmpty =>
      AppliedTypeTree(
        typeTree(TypeRef(pre, sym, Nil)),
        args map { a => typeTree(a) })
    case ClassInfoType(_, _, typeSymbol) =>
      typeTree(typeSymbol.asType.toType)
  }

  private def topLevelClassDefIsDefined = staticSymbol(macroClassName) != NoSymbol

  private lazy val macroClassName =
    "congeal.hidden." + macroName + "." + t.typeSymbol.fullName

  private def introduceTopLevelClassDef {
    val packageName = fullNameParts.dropRight(1).mkString(".")
    val className = fullNameParts.last
    val clazz = classDef(TypeName(className).toTypeName)
    c.introduceTopLevel(packageName, clazz)
  }

  private lazy val fullNameParts: List[String] = macroClassName.split('.').toList

}
