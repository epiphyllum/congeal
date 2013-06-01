package congeal

import language.experimental.macros
import scala.reflect.macros.Context

private[congeal] object macroImpls {
  
  def api[T: c.WeakTypeTag](c: Context): c.Tree =
    new ApiMacroImpl().refToTopLevelClassDefEnsureSimple[T](c)

  def impl[T: c.WeakTypeTag](c: Context): c.Tree =
    new ImplMacroImpl().refToTopLevelClassDefEnsureSimple[T](c)

  def componentApi[T: c.WeakTypeTag](c: Context): c.Tree =
    new ComponentApiMacroImpl().refToTopLevelClassDefEnsureSimple[T](c)

  def componentImpl[T: c.WeakTypeTag](c: Context): c.Tree =
    ComponentImplMacroImpl.refToTopLevelClassDefEnsureSimple[T](c)

  def hasDependency[T: c.WeakTypeTag](c: Context): c.Tree =
    HasDependencyMacroImpl.refToTopLevelClassDefEnsureSimple[T](c)

  def hasPart[T: c.WeakTypeTag](c: Context): c.Tree =
    HasPartMacroImpl.refToTopLevelClassDefEnsureSimple[T](c)

  def standsInFor[T: c.WeakTypeTag](c: Context): c.Tree =
    StandsInForMacroImpl.refToTopLevelClassDefEnsureSimple[T](c)

  def easyMock[T: c.WeakTypeTag](c: Context): c.Tree =
    EasyMockMacroImpl.refToTopLevelClassDefEnsureSimple[T](c)

}
