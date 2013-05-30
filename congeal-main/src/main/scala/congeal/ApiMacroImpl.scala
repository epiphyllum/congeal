package congeal

import language.experimental.macros
import scala.reflect.macros.Context
import scala.reflect.macros.Universe

// this is by far the nastiest macro impl, and i consider it a work in progress

/** Contains the implementation for the `api` type macro. */
private[congeal] object ApiMacroImpl extends MacroImpl {

  override protected val macroName = "api"

  override def classDef(c: Context)(t: c.Type, implClassName: c.TypeName): c.universe.ClassDef = {
    import c.universe._

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
        typeTree(c)(method.returnType),
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
