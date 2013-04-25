import language.experimental.macros

/** Contains front ends for the congeal type macros.
  *
  * All macros require type `A` to be "simple", i.e., meet the following conditions:
  * 
  *   - is a trait
  *   - is static (i.e., not a member of a method or trait. only objects all the way up.)
  *   - no non-private[this] inner classes
  *   - no members that have params or return types that derive from A
  */
package object congeal {

  /** Produces an API for the supplied type `A`.
    * @tparam A the base type to produce an API for
    */
  type api[A] = macro ApiMacroImpl.impl[A]

  /** Produces a default implementation for the supplied type `A`.
    * @tparam A the base type to produce an implementation for
    */
  type impl[A] = macro ImplMacroImpl.impl[A]

  /** Produces a component API for the supplied type `A`.
    * @tparam A the base type to produce a component API for
    */
  type componentApi[A] = macro ComponentApiMacroImpl.impl[A]

  /** Produces a component implementation for the supplied type `A`.
    * @tparam A the base type to produce a component API for
    */
  type componentImpl[A] = macro ComponentImplMacroImpl.impl[A]

  /** Indicates a dependency on the component for the supplied type `A`.
    * @tparam A the base type to indicate a dependency on
    */
  type hasDependency[A] = macro HasDependencyMacroImpl.impl[A]

}
