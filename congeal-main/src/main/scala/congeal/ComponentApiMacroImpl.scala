package congeal

import language.experimental.macros
import scala.reflect.macros.{ Context, Universe }

/** Contains the implementation for the `componentApi` type macro. */
private[congeal] object ComponentApiMacroImpl extends MacroImpl {

  override protected val macroName = "componentApi"

  override def classDef(c: Context)(t: c.Type, implClassName: c.TypeName): c.universe.ClassDef = {
    import c.universe._
    println(s"ComponentApiMacroImpl.classDef ${t.typeSymbol.fullName}")

    // FIX: supers code here is duplicated (with mods) in ComponentImplMacroImpl
    val internalSymbolTable = c.universe.asInstanceOf[scala.reflect.internal.SymbolTable]

    def hasPartParents(tt: c.Type): List[c.Type] = {
      if (tt.typeSymbol.fullName.startsWith("congeal.hidden.hasPartOf")) {

        //   val r = reify {
        //     trait Foo
        //     val w = new Foo {}
        //     val x = w.type
        //   }
        // println(showRaw(r))

        def getTheFuckingType(tname: String): c.Type = {
          val congealPackage = c.mirror.staticPackage("congeal")
          val examplesPackage = congealPackage.moduleClass.typeSignature.member(TermName("examples"))
          val basicPackage = examplesPackage.asModule.moduleClass.typeSignature.member(TermName("basic"))
          basicPackage.asModule.moduleClass.typeSignature.member(TypeName(tname)).typeSignature
        }

        val t = if (tt.typeSymbol.fullName.endsWith("UService")) {
          getTheFuckingType("UService")
        }
        else { // URepo
          getTheFuckingType("URepository")
        }
        t :: (tt.baseClasses.tail flatMap { s => hasPartParents(s.typeSignature) })
      }
      else
        (tt.baseClasses.tail flatMap { s => hasPartParents(s.typeSignature) })
    }

    val supers =
      Ident(TypeName("AnyRef")) ::
    (hasPartParents(t) map { x => ComponentImplMacroImpl.refToTopLevelClassDef(c)(x) })

    val body = if (t.declarations.filter(symbolIsNonConstructorMethod(c)(_)).isEmpty) {
      List()
    }
    else {
      // val t: api[T]
      val valName = TermName(uncapitalize(t.typeSymbol.name.toString))
      List(DefDef(Modifiers(Flag.DEFERRED),
                  valName,
                  List(),
                  List(),
                  ApiMacroImpl.refToTopLevelClassDef(c)(t),
                  EmptyTree))
    }

    // trait componentApi[T] { <body> }
    ClassDef(
      Modifiers(Flag.ABSTRACT | Flag.INTERFACE | Flag.DEFAULTPARAM),
      implClassName,
      List(),
      Template(supers,
               emptyValDef,
               body))
  }

  // TODO: this could use some work
  private def uncapitalize(s: String): String = {
    s.head.toLower +: s.tail
  }

}
