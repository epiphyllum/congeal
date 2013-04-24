package congeal

import language.experimental.macros
import scala.reflect.macros.{ Context, Universe }

/** Contains the implementation for the `componentApi` type macro. */
private[congeal] object ComponentApiImpl extends MacroImpl {

  override protected val macroName = "componentApi"

  override protected def createClassDef(c: Context)(t: c.Type, implClassName: c.TypeName): c.universe.ClassDef = {
    import c.universe._

    // ClassDef(
    //   Modifiers(ABSTRACT | INTERFACE | DEFAULTPARAM/TRAIT), TypeName("TCA"), List(),
    //   Template(List(Ident(TypeName("AnyRef"))),
    //            emptyValDef,
    //            List(DefDef(Modifiers(DEFERRED | METHOD | STABLE | ACCESSOR), TermName("t"), List(), List(), Ident(congeal.T), EmptyTree))))

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
                           SimpleApiImpl.simpleImpl(c)(t),
                           EmptyTree))))
               // List(ValDef(Modifiers(Flag.DEFERRED),
               //             valName,
               //             SimpleApiImpl.simpleImpl(c)(t),
               //             EmptyTree))))
  }

  // TODO: this could use some work
  private def uncapitalize(s: String): String = {
    s.head.toLower +: s.tail
  }

}
