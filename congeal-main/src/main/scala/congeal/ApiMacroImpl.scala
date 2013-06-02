package congeal

import language.experimental.macros
import scala.reflect.macros.Context
import scala.reflect.macros.Universe

private[congeal] object ApiMacroImpl {

  def apply(c0: Context)(t0: c0.Type) = new ApiMacroImpl {
    val c: c0.type = c0
    val t = t0
  }

  def refToTopLevelClassDef(c: Context)(t: c.Type): c.Tree =
    apply(c)(t).refToTopLevelClassDef

}

// this is by far the nastiest macro impl, and i consider it a work in progress

/** Contains the implementation for the `api` type macro. */
private[congeal] abstract class ApiMacroImpl extends MacroImpl {
  import c.universe._

  override protected val macroName = "api"

  override def classDef(implClassName: c.TypeName): ClassDef = {

    // FIX: see if you can avoid using internalSymbolTable here
    val internalSymbolTable = c.universe.asInstanceOf[scala.reflect.internal.SymbolTable]

    val publicNonConstructorMethods = t.declarations.filter { d =>
      d.isPublic &&
      symbolIsNonConstructorMethod(c)(d)
    }
    val body = publicNonConstructorMethods.map { s =>
      val method = s.asMethod

      val tparams = method.typeParams map { tp =>
        internalSymbolTable.TypeDef(tp.asInstanceOf[internalSymbolTable.Symbol]).asInstanceOf[c.universe.TypeDef]
      }
      val paramss = method.paramss map {
        _ map { p =>
          internalSymbolTable.ValDef(p.asInstanceOf[internalSymbolTable.Symbol]).asInstanceOf[c.universe.ValDef]
        }
      }

      DefDef(
        Modifiers(Flag.DEFERRED),
        s.name,
        tparams,
        paramss,
        typeTree(method.returnType),
        EmptyTree)
    }

    ClassDef(
      Modifiers(Flag.ABSTRACT | Flag.TRAIT),
      implClassName,
      Nil,
      Template(
        List(Ident(TypeName("AnyRef"))),
        emptyValDef,
        body.toList))
  }
}
