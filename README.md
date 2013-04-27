A collection of type macros intended for making Cake pattern more manageable.

The general idea of the type macros I want to implement here is described in my
blog post [Taming the Cake Pattern with Type
Macros](http://scabl.blogspot.com/2013/03/cbdi-2.html).

Macros completed so far:
  - api[A]
  - impl[A]
  - componentApi[A]
  - componentImpl[A]
  - hasDependency[A]
  - hasPart[A]

Macros still to do:
  - hasPrivatePart[A]
  - mock[A]

Other TODO items:
  - hasPart reverse lookup breaks on separate compile
  - post-hasPart add tests
  - post-hasPart refactor
  - hasDependency should expand as: trait hasDep[T] { val t: T }
  - basic documentation
  - create another project in the build called congeal-examples
  - cull TODO and FIX comments from the code
  - more tests
  - use AST and/or proxies to remove restrictions on the classes that can be congealed

Error Messages to override:

  - trait Bar { def bar: String = "bar" }
    trait Foo extends hasDependency[Bar] { def foo: String = bar.bar }
    impl[Foo]

    object creation impossible, since method bar in trait hasDependencyOfBar of type => Bar is not defined
      new impl[Foo] {}
          ^
    one error found


