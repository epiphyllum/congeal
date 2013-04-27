package congeal

import language.experimental.macros
import scala.reflect.macros.{ Context, Universe }

/** Contains the implementation for the `impl` type macro. */
private[congeal] object ImplMacroImpl extends MacroImpl {

  override protected val macroName = "impl"

  override def classDef(c: Context)(t: c.Type, implClassName: c.TypeName): c.universe.ClassDef = {
    import c.universe._

    // trait impl[T] extends T with api[T]
    ClassDef(
      Modifiers(Flag.ABSTRACT),
      implClassName,
      List(),
      Template(
        List(
          typeTree(c)(t),
          ApiMacroImpl.refToTopLevelClassDef(c)(t)),
        emptyValDef,
        List(DefDef(Modifiers(), nme.CONSTRUCTOR, List(), List(List()), TypeTree(),
                    Block(List(Apply(Select(Super(This(tpnme.EMPTY), tpnme.EMPTY), nme.CONSTRUCTOR), List())), Literal(Constant(())))))))
  }

}
