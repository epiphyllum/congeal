package congeal

import scala.reflect.macros.{ Context, Universe }

/** Basic predicates for a `Context.Symbol`. */
trait SymbolPredicates {

  protected def symbolIsNonConstructorMethod(c: Context)(s: c.Symbol) =
    s.isMethod && !s.asMethod.isConstructor

}
