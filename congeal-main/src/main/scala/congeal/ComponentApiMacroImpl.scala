package congeal

import language.experimental.macros
import scala.reflect.macros.Context
import scala.reflect.macros.Universe

/** Contains the implementation for the `componentApi` type macro. */
private[congeal] object ComponentApiMacroImpl extends MacroImpl {

  override protected val macroName = "componentApi"

  override def classDef(c: Context)(t: c.Type, implClassName: c.TypeName): c.universe.ClassDef = {
    import c.universe._

    // FIX: supers code here is duplicated (with mods) in ComponentImplMacroImpl
    def hasPartParents(tt: c.Type): List[c.Type] = {
      val hasPartTypeName = tt.typeSymbol.fullName
      val hasPartPrefix = "congeal.hidden.hasPart."
      if (hasPartTypeName.startsWith(hasPartPrefix)) {
        val underlyingTypeName = hasPartTypeName.substring(hasPartPrefix.size)
        val t = staticSymbol(c)(underlyingTypeName).typeSignature
        t :: (tt.baseClasses.tail flatMap { s => hasPartParents(s.typeSignature) })
      }
      else
        (tt.baseClasses.tail flatMap { s => hasPartParents(s.typeSignature) })
    }

    val supers =
      Ident(TypeName("AnyRef")) ::
      (hasPartParents(t) map { x => ComponentApiMacroImpl.refToTopLevelClassDef(c)(x) })

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
