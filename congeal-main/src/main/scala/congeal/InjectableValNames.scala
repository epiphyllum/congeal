package congeal

import language.experimental.macros
import scala.reflect.macros.Context
import scala.reflect.macros.Universe

private[congeal] trait InjectableValNames {

  // TODO: this could use some work
  protected def injectableValName(c: Context)(t: c.Type): c.universe.TermName = {
    def uncapitalize(s: String): String = s.head.toLower +: s.tail
    c.universe.TermName(uncapitalize(t.typeSymbol.name.toString))
  }

}
