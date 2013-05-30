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
    assert(standsInFors.size <= 1)
    val sifOption = standsInFors.headOption

    def standIns(part: c.Type): List[c.Type] = underlyingTypesOfStandsInForSupers(c)(part) match {
      case Nil => part :: Nil
      case sifs => sifs
    }
    val parts = underlyingTypesOfHasPartSupers(c)(t).flatMap({ p => standIns(p) }).toSet.toList
    val supers =
      (sifOption map { sif => ComponentApiMacroImpl.refToTopLevelClassDef(c)(sif) } getOrElse Ident(TypeName("AnyRef"))) ::
      (parts map { p => ComponentApiMacroImpl.refToTopLevelClassDef(c)(p) })

    //supers foreach { s => println(s"super $s") }

    def typeHasEmptyApi = t.declarations.filter(symbolIsNonConstructorMethod(c)(_)).isEmpty
    val body = if (typeHasEmptyApi) {
      List()
    }
    else {
      val standsInFors = underlyingTypesOfStandsInForSupers(c)(t) match {
        case Nil => t :: Nil
        case sifs => sifs
      }
      standsInFors map { sif =>
        //println(s"sif ${tfn(sif)}")
        // lazy val sif: api[Sif]
        DefDef(Modifiers(Flag.DEFERRED),
               injectableValName(c)(sif),
               List(),
               List(),
               ApiMacroImpl.refToTopLevelClassDef(c)(sif),
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
