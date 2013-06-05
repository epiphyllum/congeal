package congeal

import language.experimental.macros
import scala.reflect.macros.{ Context, Universe }

private[congeal] object ComponentImplMacroImpl {

  def apply(c0: Context)(t0: c0.Type) = new ComponentImplMacroImpl {
    val c: c0.type = c0
    val t = t0
  }

  def refToTopLevelClassDef(c0: Context)(t0: c0.Type): c0.Tree = {
    apply(c0)(t0).refToTopLevelClassDef
  }

}

/** Contains the implementation for the `componentImpl` type macro. */
private[congeal] abstract class ComponentImplMacroImpl extends MacroImpl with
  UnderlyingTypesOfSupers with InjectableValNames with StaticSymbolLookup {

  import c.universe._

  override protected val macroName = "componentImpl"

  override def classDef(implClassName: c.TypeName): ClassDef = {

    // FIX: get the order of the easyMocks and the other stuff right
    val easyMocks = underlyingTypesOfEasyMockSupers(t)

    val injections = {
      val standsInFors = underlyingTypesOfStandsInForSupers(t)
      standsInFors match {
        case Nil => List(t)
        case sifs => sifs
      }
    }
    val parts =
      underlyingTypesOfHasPartSupers(t).reverse ::: // need to reverse to get the overrides right
      underlyingTypesOfHasPrivatePartSupers(t).reverse // need to reverse to get the overrides right
    val supers =
      (injections map { i => ComponentApiMacroImpl.refToTopLevelClassDef(c)(i) }) :::
      (easyMocks map { e => ComponentApiMacroImpl.refToTopLevelClassDef(c)(e) }) :::
      (parts map { p => ComponentImplMacroImpl.refToTopLevelClassDef(c)(p) })

    val dependencies = underlyingTypesOfHasDependencySupers(t)
    val selfTypes = dependencies.map {
      d => ComponentApiMacroImpl(c)(d).refToTopLevelClassDef
    }

    def typeHasEmptyApi = t.declarations.filter(symbolIsNonConstructorMethod(c)(_)).isEmpty
    val init = DefDef(Modifiers(), TermName("$init$"), List(), List(List()), TypeTree(), Block(List(), Literal(Constant(()))))
    val body = if (typeHasEmptyApi) {
      init ::
      easyMockValDefs(easyMocks)
    }
    else {
      init ::
      injectionImplValDef(implClassName, injections, dependencies) ::
      injectionValDefs(injections) :::
      easyMockValDefs(easyMocks)
    }

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
  private def injectionImplValDef(implClassName: c.TypeName, injections: List[c.Type], dependencies: List[c.Type]): ValDef = {

    // api[Sif1] with api[Sif2] with ... with api[SifN]
    val injectionsApi = CompoundTypeTree(Template(
      injections map { i => ApiMacroImpl(c)(i).refToTopLevelClassDef },
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
              List(ImplMacroImpl(c)(t).refToTopLevelClassDef),
              emptyValDef,
              constructor :: dependencyImpls))),
        Apply(Select(New(Ident(TypeName("$anon"))), nme.CONSTRUCTOR), List())))
  }

  private def injectionValDefs(injections: List[c.Type]): List[ValDef] = {
    injections map { i =>

      // override lazy val i: api[I] = injectionImpl
      ValDef(
        Modifiers(Flag.OVERRIDE | Flag.LAZY),
        injectableValName(c)(i),
        ApiMacroImpl(c)(i).refToTopLevelClassDef,
        Block(
          List(),
          Ident(TermName("injectionImpl"))))
    }
  }

  private def easyMockValDefs(easyMocks: List[c.Type]): List[ValDef] = {
    val expandedEasyMocks = easyMocks flatMap { e =>
      underlyingTypesOfHasPartSupers(e) match {
        case Nil => List(e)
        case uts => uts
      }
    }
    expandedEasyMocks map { e =>

      val eApi = ApiMacroImpl.refToTopLevelClassDef(c)(e)

      // override lazy val e: api[E] = org.easymock.EasyMock.createMock[api[E]]
      ValDef(
        Modifiers(Flag.OVERRIDE | Flag.LAZY),
        injectableValName(c)(e),
        eApi,

        Block(
          List(),
          Apply(
            Select(
              Select(
                Select(
                  Ident(TermName("org")),
                  TermName("easymock")),
                TermName("EasyMock")),
              TermName("createMock")),
            List(
              TypeApply(
                Ident(TermName("classOf")),
                List(eApi))))))
    }
  }

}
