import language.experimental.macros

/** Contains front ends for the congeal type macros. */
package object congeal extends SimpleApiImpl with SimpleImplImpl {

  // TODO: definition of "simple" is duplicated in multiple comments

  /** Produces an API for the supplied type `A`.
    * 
    * Requires type `A` to be "simple", i.e., meet the following conditions:
    * 
    *   - is a trait
    *   - is static (i.e., not a member of a method or trait. only objects all the way up.)
    *   - no non-private[this] inner classes
    *   - no members that have params or return types that derive from A
    *
    * @tparam A the base type to produce an API for
    */
  type simpleApi[A] = macro simpleApiImpl[A]

  /** Produces a default implementation for the supplied type `A`.
    * 
    * Requires type `A` to be "simple", i.e., meet the following conditions:
    * 
    *   - is a trait
    *   - is static (i.e., not a member of a method or trait. only objects all the way up.)
    *   - no non-private[this] inner classes
    *   - no members that have params or return types that derive from A
    *
    * @tparam A the base type to produce an implementation for
    */
  type simpleImpl[A] = macro simpleImplImpl[A]

}
