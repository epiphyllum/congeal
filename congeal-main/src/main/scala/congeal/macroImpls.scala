package congeal

import language.experimental.macros
import scala.reflect.macros.Context

private[congeal] object macroImpls {
  
  private def t[T: c.WeakTypeTag](c: Context): c.Type = c.universe.weakTypeOf[T]

  def api[T: c.WeakTypeTag](c: Context): c.Tree =
    ApiMacroImpl(c)(t[T](c)).refToTopLevelClassDefEnsureSimple

  def impl[T: c.WeakTypeTag](c: Context): c.Tree =
    ImplMacroImpl(c)(t[T](c)).refToTopLevelClassDefEnsureSimple

  def componentApi[T: c.WeakTypeTag](c: Context): c.Tree =
    ComponentApiMacroImpl(c)(t[T](c)).refToTopLevelClassDefEnsureSimple

  def componentImpl[T: c.WeakTypeTag](c: Context): c.Tree =
    ComponentImplMacroImpl(c)(t[T](c)).refToTopLevelClassDefEnsureSimple

  def hasDependency[T: c.WeakTypeTag](c: Context): c.Tree =
    HasDependencyMacroImpl(c)(t[T](c)).refToTopLevelClassDefEnsureSimple

  def hasPart[T: c.WeakTypeTag](c: Context): c.Tree =
    HasPartMacroImpl(c)(t[T](c)).refToTopLevelClassDefEnsureSimple

  def standsInFor[T: c.WeakTypeTag](c: Context): c.Tree =
    StandsInForMacroImpl(c)(t[T](c)).refToTopLevelClassDefEnsureSimple

  def easyMock[T: c.WeakTypeTag](c: Context): c.Tree =
    EasyMockMacroImpl(c)(t[T](c)).refToTopLevelClassDefEnsureSimple

}
