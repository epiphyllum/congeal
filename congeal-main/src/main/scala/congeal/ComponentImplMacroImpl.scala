package congeal

import language.experimental.macros
import scala.reflect.macros.{ Context, Universe }

/** Contains the implementation for the `componentImpl` type macro. */
private[congeal] object ComponentImplMacroImpl extends MacroImpl {

  override protected val macroName = "componentImpl"

  override def classDef(c: Context)(t: c.Type, implClassName: c.TypeName): c.universe.ClassDef = {
    import c.universe._

    // FIX: supers code here is duplicated (with mods) in ComponentApiMacroImpl
    val internalSymbolTable = c.universe.asInstanceOf[scala.reflect.internal.SymbolTable]

    def hasPartParents(tt: internalSymbolTable.Type): List[String] = {
      if (tt.typeSymbol.fullName.startsWith("congeal.hidden.hasPartOf"))
        tt.typeSymbol.name.toString :: (tt.parents flatMap hasPartParents)
      else
        tt.parents flatMap hasPartParents
    }

    val supers =
      ComponentApiMacroImpl.refToTopLevelClassDef(c)(t) ::
      hasPartParents(t.asInstanceOf[internalSymbolTable.Type]).map {
        implClassName => HasPartMacroImpl.baseClass(c)(implClassName)
      }.map {
        baseClass => ComponentImplMacroImpl.refToTopLevelClassDef(c)(baseClass)
      }




    def hasDependencyParents(tt: internalSymbolTable.Type): List[String] = {
      if (tt.typeSymbol.fullName.startsWith("congeal.hidden.hasDependencyOf"))
        tt.typeSymbol.name.toString :: (tt.parents flatMap hasDependencyParents)
      else
        tt.parents flatMap hasDependencyParents
    }

    val dependencies = hasDependencyParents(t.asInstanceOf[internalSymbolTable.Type]).map {
      implClassName => HasDependencyMacroImpl.baseClass(c)(implClassName)
    }
    val selfTypes = dependencies.map {
      baseClass => ComponentApiMacroImpl.refToTopLevelClassDef(c)(baseClass)
    }


    val init = DefDef(Modifiers(), TermName("$init$"), List(), List(List()), TypeTree(), Block(List(), Literal(Constant(()))))
    val body = if (t.declarations.filter(symbolIsNonConstructorMethod(c)(_)).isEmpty) {
      List(init)
    }
    else {
      val valName = TermName(uncapitalize(t.typeSymbol.name.toString))
      List(
        init,

        // override lazy val t: api[T] = new impl[T] { lazy val t: api[T] = outer.t }
        ValDef(
          Modifiers(Flag.OVERRIDE | Flag.LAZY),
          valName,
          ApiMacroImpl.refToTopLevelClassDef(c)(t),
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
                      val selfTypeValName = uncapitalize(d.typeSymbol.name.toString)
                      DefDef(
                        Modifiers(Flag.OVERRIDE),
                        TermName(selfTypeValName),
                        List(),
                        List(List()),
                        TypeTree(),
                        //typeTree(c)(d),
                        Select(This(implClassName), TermName(selfTypeValName)))
                    })

                  ))),

            Apply(Select(New(Ident(TypeName("$anon"))), nme.CONSTRUCTOR), List()))))
    }

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

  // TODO: this could use some work
  // FIX: duplicated in ComponentApiImpl
  private def uncapitalize(s: String): String = {
    s.head.toLower +: s.tail
  }

}
