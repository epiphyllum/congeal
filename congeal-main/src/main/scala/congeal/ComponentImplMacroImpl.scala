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

    val injections = {
      val standsInFors = underlyingTypesOfStandsInForSupers(c)(t)
      standsInFors match {
        case Nil => List(t)
        case sifs => sifs
      }
    }
    val parts = underlyingTypesOfHasPartSupers(c)(t).reverse // need to reverse to get the overrides right
    val supers =
      (injections map { i => ComponentApiMacroImpl.refToTopLevelClassDef(c)(i) }) :::
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
      init ::
      injectionImplValDef(c)(t, implClassName, injections, dependencies) ::
      injectionValDefs(c)(injections)
    }

    //println(s"LEAVE ComponentImplMacroImpl ${tfn(t)}")

    // trait componentImpl[T] extends <supers> {
    //   <body>
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

  // private lazy val injectionImpl: api[Sif1] with api[Sif2] with ... with api[SifN] = new impl[T] {
  //   lazy val dep1: api[Dep1] = outer.dep1
  //   lazy val dep2: api[Dep2] = outer.dep2
  //   ...
  //   lazy val depN: api[DepN] = outer.depN
  // }
  private def injectionImplValDef(
    c: Context)(
    t: c.Type,
    implClassName: c.TypeName,
    injections: List[c.Type],
    dependencies: List[c.Type]): c.universe.ValDef = {
    import c.universe._

    // api[Sif1] with api[Sif2] with ... with api[SifN]
    val injectionsApi = CompoundTypeTree(Template(
      injections map { i => ApiMacroImpl.refToTopLevelClassDef(c)(i) },
      emptyValDef,
      List()))

    // def this() { super() }
    val constructor = DefDef(
      Modifiers(),
      nme.CONSTRUCTOR,
      List(),
      List(List()),
      TypeTree(),
      Block(
        List(Apply(Select(Super(This(tpnme.EMPTY), tpnme.EMPTY), nme.CONSTRUCTOR), List())), // super()
        Literal(Constant(()))))

    // lazy val dep1: api[Dep1] = outer.dep1
    // lazy val dep2: api[Dep2] = outer.dep2
    // ...
    // lazy val depN: api[DepN] = outer.depN
    val dependencyImpls = dependencies map { d =>
      DefDef(
        Modifiers(Flag.OVERRIDE),
        injectableValName(c)(d),
        List(),
        List(List()),
        TypeTree(),
        Select(This(implClassName), injectableValName(c)(d)))
    }

    ValDef(
      Modifiers(Flag.PRIVATE | Flag.LAZY | Flag.FINAL),
      TermName("injectionImpl"),
      injectionsApi,
      Block(
        List(
          ClassDef(
            Modifiers(Flag.FINAL),
            TypeName("$anon"),
            List(),
            Template(
              List(ImplMacroImpl.refToTopLevelClassDef(c)(t)),
              emptyValDef,
              constructor :: dependencyImpls))),
              //constructor :: Nil))),
        Apply(Select(New(Ident(TypeName("$anon"))), nme.CONSTRUCTOR), List())))
  }

  private def injectionValDefs(
    c: Context)(
    injections: List[c.Type]): List[c.universe.ValDef] = {
    import c.universe._
    injections map { i =>

      // override lazy val i: api[I] = injectionImpl
      ValDef(
        Modifiers(Flag.OVERRIDE | Flag.LAZY),
        injectableValName(c)(i),
        ApiMacroImpl.refToTopLevelClassDef(c)(i),
        Block(
          List(),
          Ident(TermName("injectionImpl"))))
    }
  }

}
