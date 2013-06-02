package congeal

import language.experimental.macros
import scala.reflect.macros.Context
import scala.reflect.macros.Universe

private[congeal] object ImplMacroImpl {

  def apply(c0: Context)(t0: c0.Type) = new ImplMacroImpl {
    val c: c0.type = c0
    val t = t0
  }

}

/** Contains the implementation for the `impl` type macro. */
private[congeal] abstract class ImplMacroImpl extends MacroImpl {

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
          ApiMacroImpl(c)(t).refToTopLevelClassDef),
        emptyValDef,
        List(DefDef(Modifiers(), nme.CONSTRUCTOR, List(), List(List()), TypeTree(),
                    Block(List(Apply(Select(Super(This(tpnme.EMPTY), tpnme.EMPTY), nme.CONSTRUCTOR), List())), Literal(Constant(())))))))
  }

}
