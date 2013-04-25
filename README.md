A collection of type macros intended for making Cake pattern more manageable.

The general idea of the type macros I want to implement here is described in my
blog post [Taming the Cake Pattern with Type
Macros](http://scabl.blogspot.com/2013/03/cbdi-2.html).

Macros completed so far:
  - api[A]
  - impl[A]
  - componentApi[A]
  - componentImpl[A]

Macros still to do:
  - hasPart[A]
  - hasDependency[A]
  - hasPrivatePart[A]
  - mock[A]

Other TODO items:
  - basic documentation
  - use AST and/or proxies to remove restrictions on the classes that can be congealed
  - create another project in the build called congeal-examples
  - cull TODO and FIX comments from the code
