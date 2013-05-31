package congeal

import language.experimental.macros
import scala.reflect.macros.Context
import scala.reflect.macros.Universe

/** Contains the implementation for the `componentApi` type macro. */
private[congeal] object ComponentApiMacroImpl extends MacroImpl with UnderlyingTypesOfSupers with InjectableValNames {

  override protected val macroName = "componentApi"

  override def classDef(c: Context)(t: c.Type, implClassName: c.TypeName): c.universe.ClassDef = {
    import c.universe._

    def tfn(t: c.Type): String = t.typeSymbol.fullName
    //println(s"ComponentApiMacroImpl ${tfn(t)}")

    val standsInFors = underlyingTypesOfStandsInForSupers(c)(t)
    val easyMocks = underlyingTypesOfEasyMockSupers(c)(t)
    val parts = {
      def standInsForPart(part: c.Type): List[c.Type] = underlyingTypesOfStandsInForSupers(c)(part) match {
        case Nil => part :: Nil
        case sifs => sifs
      }
      underlyingTypesOfHasPartSupers(c)(t).flatMap({ p => standInsForPart(p) }).toSet.toList
    }
    val supers = (standsInFors ::: easyMocks ::: parts) map { s => ComponentApiMacroImpl.refToTopLevelClassDef(c)(s) }

    //supers foreach { s => println(s"super $s") }

    def typeHasEmptyApi = t.declarations.filter(symbolIsNonConstructorMethod(c)(_)).isEmpty
    val body = if (typeHasEmptyApi) {
      List()
    }
    else {
      val injections = standsInFors match {
        case Nil => List(t)
        case sifs => sifs
      }
      injections map { i =>
        //println(s"injection ${tfn(i)}")

        // lazy val sif: api[Sif]
        DefDef(Modifiers(Flag.DEFERRED),
               injectableValName(c)(i),
               List(),
               List(),
               ApiMacroImpl.refToTopLevelClassDef(c)(i),
               EmptyTree)
      }
    }

    //println(s"LEAVE ComponentApiMacroImpl ${tfn(t)}")

    // trait componentApi[T] { <body> }
    ClassDef(
      Modifiers(Flag.ABSTRACT | Flag.INTERFACE | Flag.DEFAULTPARAM),
      implClassName,
      List(),
      Template(supers,
               emptyValDef,
               body))
  }

}
