import language.experimental.macros
import scala.reflect.macros.Context

/** Contains implementations of the congeal type macros. */
package object congeal {

  /** Produces an API for the supplied type `A`. */
  type api[A] = macro apiImpl[A]

  def apiImpl[T: c.WeakTypeTag](c: Context) = {
    import c.universe._
    Ident(TypeName("C"))

    // val name = c.freshName(c.enclosingImpl.name).toTypeName
    // val clazz = typeOf[T] //ClassDef(..., Template(..., generateCode()))
    // c.introduceTopLevel(c.enclosingPackage.pid.toString, clazz)
    // val classRef = Select(c.enclosingPackage.pid, name)
    // Apply(classRef, List(Literal(Constant(c.eval(url)))))
  }

  class C { def bar = println("hello world") }

}
