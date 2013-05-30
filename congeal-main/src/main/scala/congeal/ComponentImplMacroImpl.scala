package congeal

import language.experimental.macros
import scala.reflect.macros.{ Context, Universe }

/** Contains the implementation for the `componentImpl` type macro. */
private[congeal] object ComponentImplMacroImpl extends MacroImpl with UnderlyingTypesOfSupers with InjectableValNames {

  override protected val macroName = "componentImpl"

  override def classDef(c: Context)(t: c.Type, implClassName: c.TypeName): c.universe.ClassDef = {
    import c.universe._

    //def tfn(t: c.Type): String = t.typeSymbol.fullName
    //println(s"ComponentImplMacroImpl ${tfn(t)}")

    val standsInFors = underlyingTypesOfStandsInForSupers(c)(t) match {
      case Nil => t :: Nil
      case sifs => sifs
    }
    assert(standsInFors.size == 1)
    val sif = standsInFors.head

    val parts = underlyingTypesOfHasPartSupers(c)(t).reverse
    val supers =
      ComponentApiMacroImpl.refToTopLevelClassDef(c)(sif) ::
      (parts map { p => ComponentImplMacroImpl.refToTopLevelClassDef(c)(p) })

    //supers foreach { s => println(s"super $s") }

    val dependencies = underlyingTypesOfHasDependencySupers(c)(t)
    val selfTypes = dependencies.map {
      d => ComponentApiMacroImpl.refToTopLevelClassDef(c)(d)
    }

    def typeHasEmptyApi = t.declarations.filter(symbolIsNonConstructorMethod(c)(_)).isEmpty
    val init = DefDef(Modifiers(), TermName("$init$"), List(), List(List()), TypeTree(), Block(List(), Literal(Constant(()))))
    val body = if (typeHasEmptyApi) {
      List(init)
    }
    else {
      List(
        init,

        // override lazy val sif: api[Sif] = new impl[T] { lazy val dep: api[Dep] = outer.dep }
        ValDef(
          Modifiers(Flag.OVERRIDE | Flag.LAZY),
          injectableValName(c)(sif),
          ApiMacroImpl.refToTopLevelClassDef(c)(sif),
          Block(
            List(
              ClassDef(
                Modifiers(Flag.FINAL),
                TypeName("$anon"),
                List(),
                Template(
                  List(ImplMacroImpl.refToTopLevelClassDef(c)(t)),
                  emptyValDef,

                  // def this() { super() }
                  DefDef(
                    Modifiers(),
                    nme.CONSTRUCTOR,
                    List(),
                    List(List()),
                    TypeTree(),
                    Block(
                      List(Apply(Select(Super(This(tpnme.EMPTY), tpnme.EMPTY), nme.CONSTRUCTOR), List())), // super()
                      Literal(Constant(()))))

                    ::

                    (dependencies map { d =>
                      val selfTypeValName = injectableValName(c)(d)
                      DefDef(
                        Modifiers(Flag.OVERRIDE),
                        selfTypeValName,
                        List(),
                        List(List()),
                        TypeTree(),
                        Select(This(implClassName), selfTypeValName))
                    })

                  ))),

            Apply(Select(New(Ident(TypeName("$anon"))), nme.CONSTRUCTOR), List()))))
    }

    //println(s"LEAVE ComponentImplMacroImpl ${tfn(t)}")

    // trait componentImpl[T] extends componentApi[T] {
    //   override lazy val t: api[T] = new impl[T] {}
    // }
    ClassDef(
      Modifiers(Flag.ABSTRACT | Flag.DEFAULTPARAM),
      implClassName,
      List(),
      Template(
        supers,
        ValDef(
          Modifiers(Flag.PRIVATE),
          TermName("self"),
          CompoundTypeTree(Template(selfTypes, emptyValDef, List())),
          EmptyTree),
        body))
  }

}
