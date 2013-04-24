package congeal

import language.experimental.macros
import scala.reflect.macros.{ Context, Universe }

/** Contains the implementation for the `componentApi` type macro. */
private[congeal] object ComponentApiImpl extends MacroImpl {

  /** Provides implementation of type macro `congeal.componentApi`. */
  def componentApiImpl[T: c.WeakTypeTag](c: Context): c.Tree = impl[T](c)

  /** Produces a tree for the componentApi of the supplied type t. */
  protected def componentApiTree(c: Context)(t: c.Type): c.Tree = implTree(c)(t)

  override protected val macroName = "componentApi"

  override protected def createClassDef(c: Context)(t: c.Type, implClassName: c.TypeName): c.universe.ClassDef = {
    import c.universe._
    val valName = TermName(uncapitalize(t.typeSymbol.name.toString))
    ClassDef(
      Modifiers(Flag.ABSTRACT | Flag.INTERFACE | Flag.DEFAULTPARAM), implClassName, List(),
      Template(List(Ident(TypeName("AnyRef"))),
               emptyValDef,
               List(ValDef(Modifiers(Flag.DEFERRED),
                           valName,
                           SimpleApiImpl.simpleApiTree(c)(t),
                           EmptyTree))))
  }

  // TODO: this could use some work
  private def uncapitalize(s: String): String = {
    s.head.toLower +: s.tail
  }

}
