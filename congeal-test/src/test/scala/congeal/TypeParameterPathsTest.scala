package congeal

import congeal.sc._

import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/** JUnit tests to assure that macros work for any type parameters that are static
  * traits. "Static trait" means that the trait can be contained in packages and
  * objects, but cannot be nested within any methods, classes or traits.
  *
  * We only need to test on a single macro, since they all share the same code for
  * loading static classes.
  */
@RunWith(classOf[JUnit4])
class TypeParameterPathsTest {

  @Test
  def apiWorksInDefaultPackage() {
    compilingSourceSucceeds(
      """|import congeal.api
         |case class U(uName: String)
         |trait URepository {
         |   def getU(uName: String): Option[U] = None // STUB
         |}
         |trait URepositoryApi extends api[URepository]
      |""".stripMargin)
  }

  @Test
  def apiWorksInNonDefaultPackageOneLevelDeep() {
    compilingSourceSucceeds(
      """|package foo
         |import congeal.api
         |case class U(uName: String)
         |trait URepository {
         |   def getU(uName: String): Option[U] = None // STUB
         |}
         |trait URepositoryApi extends api[URepository]
      |""".stripMargin)
  }

  @Test
  def apiWorksInNonDefaultPackageMoreThanOneLevelDeep() {
    compilingSourceSucceeds(
      """|package foo.bar
         |import congeal.api
         |case class U(uName: String)
         |trait URepository {
         |   def getU(uName: String): Option[U] = None // STUB
         |}
         |trait URepositoryApi extends api[URepository]
      |""".stripMargin)
  }

  @Test
  def apiWorksInObjectInDefaultPackage() {
    compilingSourceSucceeds(
      """|import congeal.api
         |object Foo {
         |  case class U(uName: String)
         |  trait URepository {
         |     def getU(uName: String): Option[U] = None // STUB
         |  }
         |  trait URepositoryApi extends api[URepository]
         |}
      |""".stripMargin)
  }

  @Test
  def apiWorksInObjectInNonDefaultPackageOneLevelDeep() {
    compilingSourceSucceeds(
      """|package foo
         |import congeal.api
         |object Foo {
         |  case class U(uName: String)
         |  trait URepository {
         |     def getU(uName: String): Option[U] = None // STUB
         |  }
         |  trait URepositoryApi extends api[URepository]
         |}
      |""".stripMargin)
  }

  @Test
  def apiWorksInObjectInNonDefaultPackageMoreThanOneLevelDeep() {
    compilingSourceSucceeds(
      """|package foo.bar
         |import congeal.api
         |object Foo {
         |  case class U(uName: String)
         |  trait URepository {
         |     def getU(uName: String): Option[U] = None // STUB
         |  }
         |  trait URepositoryApi extends api[URepository]
         |}
      |""".stripMargin)
  }

  @Test
  def apiWorksInObjectNestedInObjectInDefaultPackage() {
    compilingSourceSucceeds(
      """|import congeal.api
         |object Foo {
         |  object Bar {
         |    case class U(uName: String)
         |    trait URepository {
         |       def getU(uName: String): Option[U] = None // STUB
         |    }
         |    trait URepositoryApi extends api[URepository]
         |  }
         |}
      |""".stripMargin)
  }

  @Test
  def apiWorksInObjectNestedInObjectInNonDefaultPackageOneLevelDeep() {
    compilingSourceSucceeds(
      """|package foo
         |import congeal.api
         |object Foo {
         |  object Bar {
         |    case class U(uName: String)
         |    trait URepository {
         |       def getU(uName: String): Option[U] = None // STUB
         |    }
         |    trait URepositoryApi extends api[URepository]
         |  }
         |}
      |""".stripMargin)
  }

  @Test
  def apiWorksInObjectNestedInObjectInNonDefaultPackageMoreThanOneLevelDeep() {
    compilingSourceSucceeds(
      """|package foo.bar
         |import congeal.api
         |object Foo {
         |  case class U(uName: String)
         |  trait URepository {
         |     def getU(uName: String): Option[U] = None // STUB
         |  }
         |  trait URepositoryApi extends api[URepository]
         |}
      |""".stripMargin)
  }

}
