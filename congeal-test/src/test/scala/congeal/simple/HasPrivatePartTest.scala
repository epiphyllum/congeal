package congeal.simple

import congeal.sc._

import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/** JUnit tests for basic usage of the `congeal.hasPrivatePart` macro. */
@RunWith(classOf[JUnit4])
class HasPrivatePartTest {

  @Test
  def hasPrivatePartSaysHello() {
    compilingSourceProducesAppWithOutput(
      """|import congeal._
         |trait Letter
         |trait S extends Letter
         |trait SFactory {
         |  def createLetter: S = {
         |    println("SFactory.createLetter")
         |    new S {}
         |  }
         |}
         |trait LetterFactory extends hasDependency[SFactory] {
         |  def createLetter: Letter = {
         |    println("LetterFactory.createLetter")
         |    sFactory.createLetter
         |  }
         |}
         |trait Factory extends hasPart[LetterFactory] with hasPrivatePart[SFactory]
         |trait Application extends componentApi[Factory] {
         |  letterFactory.createLetter
         |}
         |object Test extends App {
         |  new Application with componentImpl[Factory] {}
         |}
      |""".stripMargin,
      "Test",
      "LetterFactory.createLetter\nSFactory.createLetter\n")
  }

  @Test
  def cannotAccessPrivatePartFromComponentApi() {
    compilingSourceErrorsWithMessage(
      """|import congeal._
         |trait Letter
         |trait S extends Letter
         |trait SFactory {
         |  def createLetter: S = {
         |    println("SFactory.createLetter")
         |    new S {}
         |  }
         |}
         |trait LetterFactory extends hasDependency[SFactory] {
         |  def createLetter: Letter = {
         |    println("LetterFactory.createLetter")
         |    sFactory.createLetter
         |  }
         |}
         |trait Factory extends hasPart[LetterFactory] with hasPrivatePart[SFactory]
         |trait Application extends componentApi[Factory] {
         |  sFactory.createLetter
         |}
      |""".stripMargin,
      """|source.scala:18: error: not found: value sFactory
         |  sFactory.createLetter
         |  ^
         |one error found
      |""".stripMargin)
  }

}
