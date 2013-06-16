A collection of macro annotations for doing dependency injection.

Under the hood, congeal uses the cake pattern.

The general idea of the type macros I want to implement here is described in my
blog post [Taming the Cake Pattern with Type
Macros](http://scabl.blogspot.com/2013/03/cbdi-2.html).

At the moment, these are still type macros.

At the moment, congeal only builds against experimental "Kepler" branch of Scala.

Fair warning: one of the three kepler commits from Jun 02 & Jun 03 broke my
tests. examples still run. i have a workaround in place until i have a
moment to address this. https://github.com/scalamacros/kepler/commits/master

All macros require type `A` to be "simple", i.e., meet the following conditions:

  - is a trait
  - is static (i.e., not a member of a method or trait. only objects all the way up.)
  - no non-private[this] inner classes
  - no members that have params or return types that derive from A

Macros completed so far:
  - api[A]
  - impl[A]
  - componentApi[A]
  - componentImpl[A]
  - hasDependency[A]
  - hasPart[A]
  - standsInFor[A]
  - hasPrivatePart[A]

Macros still to do:
  - mock[A]
    - have initial implementation of easyMock[A]. need to generalize

My planning board is here:
  - https://www.pivotaltracker.com/s/projects/850181
