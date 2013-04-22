import language.experimental.macros

/** Contains front ends for the congeal type macros. */
package object congeal extends SimpleApiImpl {

  /** Produces an API for the supplied type `A`.
    * 
    * Requires type `A` to be "simple", i.e., meet the following conditions:
    * 
    *   - is a trait
    *   - no non-private[this] inner classes
    *   - no members that have params or return types that derive from A
    *
    * @tparam A the base type to produce an API for
    */
  type simpleApi[A] = macro simpleApiImpl[A]

}
