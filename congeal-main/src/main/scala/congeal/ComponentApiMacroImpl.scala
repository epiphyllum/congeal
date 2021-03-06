package congeal

import language.experimental.macros
import scala.reflect.macros.Context
import scala.reflect.macros.Universe

private[congeal] object ComponentApiMacroImpl {

  def apply(c0: Context)(t0: c0.Type) = new ComponentApiMacroImpl {
    val c: c0.type = c0
    val t = t0
  }

  def refToTopLevelClassDef(c: Context)(t: c.Type): c.Tree = {
    apply(c)(t).refToTopLevelClassDef
  }

}

/** Contains the implementation for the `componentApi` type macro. */
private[congeal] abstract class ComponentApiMacroImpl extends MacroImpl with
  UnderlyingTypesOfSupers with InjectableValNames {

  import c.universe._

  override protected val macroName = "componentApi"

  override def classDef(implClassName: c.TypeName): ClassDef = {
    val standsInFors = underlyingTypesOfStandsInForSupers(t)
    val easyMocks = underlyingTypesOfEasyMockSupers(t)
    val parts = {
      def standInsForPart(part: c.Type): List[c.Type] = underlyingTypesOfStandsInForSupers(part) match {
        case Nil => part :: Nil
        case sifs => sifs
      }
      underlyingTypesOfHasPartSupers(t).flatMap({ p => standInsForPart(p) }).toSet.toList
    }
    val supers = (standsInFors ::: easyMocks ::: parts) map {
      s => ComponentApiMacroImpl(c)(s).refToTopLevelClassDef
    }

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

        // lazy val sif: api[Sif]
        DefDef(Modifiers(Flag.DEFERRED),
               injectableValName(c)(i),
               List(),
               List(),
               ApiMacroImpl(c)(i).refToTopLevelClassDef,
               EmptyTree)
      }
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

}
