package congeal

import language.experimental.macros
import scala.reflect.macros.{ Context, Universe }

/** Contains the implementation for the `simpleImpl` type macro. */
private[congeal] object SimpleImplImpl extends MacroImpl {

  override protected val macroName = "simpleImpl"

  override protected def createClassDef(c: Context)(t: c.Type, implClassName: c.TypeName): c.universe.ClassDef = {
    import c.universe._

    // trait simpleImpl[T] extends T with simpleApi[T]
    ClassDef(Modifiers(), implClassName, List(), Template(
      List(
        typeTree(c)(t),
        SimpleApiImpl.simpleImpl(c)(t)),
      emptyValDef,
      List(DefDef(Modifiers(), nme.CONSTRUCTOR, List(), List(List()), TypeTree(),
                  Block(List(Apply(Select(Super(This(tpnme.EMPTY), tpnme.EMPTY), nme.CONSTRUCTOR), List())), Literal(Constant(())))))))
  }

}
