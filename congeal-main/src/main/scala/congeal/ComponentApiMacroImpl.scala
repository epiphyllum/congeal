package congeal

import language.experimental.macros
import scala.reflect.macros.Context
import scala.reflect.macros.Universe

/** Contains the implementation for the `componentApi` type macro. */
private[congeal] object ComponentApiMacroImpl extends MacroImpl with UnderlyingTypesOfSupers with InjectableValNames {

  override protected val macroName = "componentApi"

  override def classDef(c: Context)(t: c.Type, implClassName: c.TypeName): c.universe.ClassDef = {
    import c.universe._

    val supers =
      Ident(TypeName("AnyRef")) ::
      (underlyingTypesOfHasPartSupers(c)(t) map { x => ComponentApiMacroImpl.refToTopLevelClassDef(c)(x) })

    def typeHasEmptyApi = t.declarations.filter(symbolIsNonConstructorMethod(c)(_)).isEmpty
    val body = if (typeHasEmptyApi) {
      List()
    }
    else {
      List(DefDef(Modifiers(Flag.DEFERRED),
                  injectableValName(c)(t),
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

}
