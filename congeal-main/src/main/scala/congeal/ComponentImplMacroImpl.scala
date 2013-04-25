package congeal

import language.experimental.macros
import scala.reflect.macros.{ Context, Universe }

/** Contains the implementation for the `componentImpl` type macro. */
private[congeal] object ComponentImplMacroImpl extends MacroImpl {

  override protected val macroName = "componentImpl"

  override protected def createClassDef(c: Context)(t: c.Type, implClassName: c.TypeName): c.universe.ClassDef = {
    import c.universe._

    // trait componentImpl[T] extends componentApi[T] {
    //   override lazy val t: api[T] = new impl[T] {}
    // }
    val valName = TermName(uncapitalize(t.typeSymbol.name.toString))
    ClassDef(
      Modifiers(Flag.ABSTRACT | Flag.DEFAULTPARAM),
      implClassName,
      List(),
      Template(
        List(ComponentApiMacroImpl.simpleImpl(c)(t)),
        emptyValDef,
        List(
          DefDef(Modifiers(), TermName("$init$"), List(), List(List()), TypeTree(), Block(List(), Literal(Constant(())))),
          ValDef( // override lazy val t: api[T] = new impl[T] {}
            Modifiers(Flag.OVERRIDE | Flag.LAZY),
            valName,
            TypeTree(),
            Block(
              List(
                ClassDef(
                  Modifiers(Flag.FINAL),
                  TypeName("$anon"),
                  List(),
                  Template(
                    List(ImplMacroImpl.simpleImpl(c)(t)),
                    emptyValDef,
                    List(
                      // def this() { super() }
                      DefDef(
                        Modifiers(),
                        nme.CONSTRUCTOR,
                        List(),
                        List(List()),
                        TypeTree(),
                        Block(
                          List(Apply(Select(Super(This(tpnme.EMPTY), tpnme.EMPTY), nme.CONSTRUCTOR), List())), // super()
                          Literal(Constant(())))))))),
              Apply(Select(New(Ident(TypeName("$anon"))), nme.CONSTRUCTOR), List()))))))
  }

  // TODO: this could use some work
  // FIX: duplicated in ComponentApiImpl
  private def uncapitalize(s: String): String = {
    s.head.toLower +: s.tail
  }

}
