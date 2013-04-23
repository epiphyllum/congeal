package congeal

import scala.reflect.macros.{ Context, Universe }

/** Basic predicates for a `Context.Symbol`. */
private[congeal] trait SymbolPredicates {

  // TODO: move other sym preds here

  protected def symbolIsNonConstructorMethod(c: Context)(s: c.Symbol) =
    s.isMethod && !s.asMethod.isConstructor

}
