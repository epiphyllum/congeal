package congeal.examples.privateparts

import congeal._

trait Letter

case class S(sName: String) extends Letter

case class T(tName: String) extends Letter

case class U(uName: String) extends Letter

trait SFactory {
  def createLetter(name: String): S = S(name)
}

trait TFactory {
  def createLetter(name: String): T = T(name)
}

trait UFactory {
  def createLetter(name: String): U = U(name)
}

trait LetterFactory extends
  hasDependency[SFactory] with
  hasDependency[TFactory] with
  hasDependency[UFactory] {

  def createLetter(letter: Char, name: String): Letter = letter match {
    case 'S' => sFactory.createLetter(name)
    case 'T' => tFactory.createLetter(name)
    case 'U' => uFactory.createLetter(name)
    case _ => throw new Error
  }
}

trait Factory extends
  hasPart[LetterFactory] with
  hasPrivatePart[SFactory] with
  hasPrivatePart[TFactory] with
  hasPrivatePart[UFactory]

abstract class Application extends componentApi[Factory] {
  println(letterFactory.createLetter('S', "testName"))

  // uncomment me for compiler error:
  // println(sFactory.createLetter("testName"))
}

// outputs S(testName)
object ApplicationTest extends App {
  new Application with componentImpl[Factory]
}
