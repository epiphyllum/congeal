import language.experimental.macros
import scala.reflect.macros.Context

/** Contains implementations of the congeal type macros. */
package object congeal {

  /** Produces an API for the supplied type `A`. */
  type api[A] = macro apiImpl[A]

  def apiImpl[T: c.WeakTypeTag](c: Context) = {
    import c.universe._
    c.universe.Ident(c.universe.TypeName("C"))
  }

  class C { def bar = println("hello world") }

}
