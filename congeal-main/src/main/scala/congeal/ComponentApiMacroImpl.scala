package congeal

import language.experimental.macros
import scala.reflect.macros.{ Context, Universe }

/** Contains the implementation for the `componentApi` type macro. */
private[congeal] object ComponentApiMacroImpl extends MacroImpl {

  override protected val macroName = "componentApi"

  override protected def createClassDef(c: Context)(t: c.Type, implClassName: c.TypeName): c.universe.ClassDef = {
    import c.universe._

    // trait componentApi[T] { val t: api[T] }
    val valName = TermName(uncapitalize(t.typeSymbol.name.toString))
    ClassDef(
      Modifiers(Flag.ABSTRACT | Flag.INTERFACE | Flag.DEFAULTPARAM), implClassName, List(),
      Template(List(Ident(TypeName("AnyRef"))),
               emptyValDef,
               List(DefDef(Modifiers(Flag.DEFERRED),
                           valName,
                           List(),
                           List(),
                           ApiMacroImpl.simpleImpl(c)(t),
                           EmptyTree))))
  }

  // TODO: this could use some work
  private def uncapitalize(s: String): String = {
    s.head.toLower +: s.tail
  }

}
