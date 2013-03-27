import language.experimental.macros
import scala.reflect.macros.Context

/** Contains implementations of the congeal type macros. */
package object congeal {

  /** Produces an API version of the supplied type `A`. */
  type api[A] = macro apiImpl

  def apiImpl(c: Context) = c.universe.Ident(c.universe.TypeName("C"))

  trait C { def hello = println("hello world") }

}
