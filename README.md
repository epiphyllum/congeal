A collection of type macros intended for making Cake pattern more manageable.

The general idea of the type macros I want to implement here is described in my
blog post [Taming the Cake Pattern with Type
Macros](http://scabl.blogspot.com/2013/03/cbdi-2.html).

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

Macros still to do:
  - overridesPart[A]?
  - mock[A]
  - hasPrivatePart[A]

Other TODO items:
  - post-hasPart refactor: MacroImpl subclasses
  - cull TODO and FIX comments from the code

  - fill out congeal-examples
  - basic documentation
  - use AST and/or proxies to remove restrictions on the classes that can be congealed
  - bug: putting injectables anywhere under package congeal causes trouble
    - NOTE this does not happen with package congeal, but does with package.congeal.examples.basic

Error Messages to override:

  - trait Bar { def bar: String = "bar" }
    trait Foo extends hasDependency[Bar] { def foo: String = bar.bar }
    impl[Foo]

    object creation impossible, since method bar in trait hasDependencyOfBar of type => Bar is not defined
      new impl[Foo] {}
          ^
    one error found


