package congeal

import language.experimental.macros
import scala.reflect.macros.{ Context, Universe }

/** Contains the implementation for the `componentApi` type macro. */
private[congeal] object ComponentApiMacroImpl extends MacroImpl {

  override protected val macroName = "componentApi"

  override def classDef(c: Context)(t: c.Type, implClassName: c.TypeName): c.universe.ClassDef = {
    import c.universe._

    // FIX: supers code here is duplicated (with mods) in ComponentImplMacroImpl
    val internalSymbolTable = c.universe.asInstanceOf[scala.reflect.internal.SymbolTable]

    def hasPartParents(tt: internalSymbolTable.Type): List[String] = {
      if (tt.typeSymbol.fullName.startsWith("congeal.hidden.hasPartOf"))
        tt.typeSymbol.name.toString :: (tt.parents flatMap hasPartParents)
      else
        tt.parents flatMap hasPartParents
    }

    val supers =
      Ident(TypeName("AnyRef")) ::
      hasPartParents(t.asInstanceOf[internalSymbolTable.Type]).map {
        implClassName => HasPartMacroImpl.baseClass(c)(implClassName)
      }.map {
        baseClass => ComponentApiMacroImpl.refToTopLevelClassDef(c)(baseClass)
      }

    val body = if (t.declarations.filter(symbolIsNonConstructorMethod(c)(_)).isEmpty) {
      List()
    }
    else {
      // val t: api[T]
      val valName = TermName(uncapitalize(t.typeSymbol.name.toString))
      List(DefDef(Modifiers(Flag.DEFERRED),
                  valName,
                  List(),
                  List(),
                  ApiMacroImpl.refToTopLevelClassDef(c)(t),
                  EmptyTree))
    }

    // trait componentApi[T] { <body> }
    ClassDef(
      Modifiers(Flag.ABSTRACT | Flag.INTERFACE | Flag.DEFAULTPARAM),
      implClassName,
      List(),
      Template(supers,
               emptyValDef,
               body))
  }

  // TODO: this could use some work
  private def uncapitalize(s: String): String = {
    s.head.toLower +: s.tail
  }

}
