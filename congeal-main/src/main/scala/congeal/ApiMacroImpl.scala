package congeal

import language.experimental.macros
import scala.reflect.macros.{ Context, Universe }

/** Contains the implementation for the `api` type macro. */
private[congeal] object ApiMacroImpl extends MacroImpl {

  override protected val macroName = "api"

  override def classDef(c: Context)(t: c.Type, implClassName: c.TypeName): c.universe.ClassDef = {
    import c.universe._
    val internalSymbolTable = c.universe.asInstanceOf[scala.reflect.internal.SymbolTable]

    // FIX: should be members not declarations
    val body = t.declarations.filter(symbolIsNonConstructorMethod(c)(_)).map { s =>
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
        Modifiers(Flag.DEFERRED), // FIX: match protected/public/whatever of copied method
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
