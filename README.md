A collection of type macros intended for making Cake pattern more manageable.

The general idea of the type macros I want to implement here is described in my
blog post [Taming the Cake Pattern with Type
Macros](http://scabl.blogspot.com/2013/03/cbdi-2.html).

I'm just getting started here, as you can probably see.

Roadmap:
  - simpleApi[A]
    - a simple version of api[A] - do whatever is possible without looking at AST
    - initial limitations:
      - only traits
      - no non-private[this] inner classes
      - no members that have params or return types that derive from A
    - possibly loosen these restrictions later, but for now, just do what I need
      for componentApi and componentImpl
    - DONE!
  - simpleImpl[A]
    - a simple version of impl[A] - do whatever is possible without looking at AST
    - same initial restrictions as simpleApi[A]
    - DONE!
  - componentApi[A]
  - componentImpl[A]
  - hasPart
  - hasDependency
  - hasPrivatePart
  - mock
  - api[A] - requires AST
  - impl[A] - requires AST
