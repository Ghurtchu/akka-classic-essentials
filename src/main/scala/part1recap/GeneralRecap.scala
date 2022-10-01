package part1recap

import scala.annotation.tailrec

object GeneralRecap extends scala.App {

  val aCondition: Boolean = false
  // aCondition = true reassignment on vals are forbidden

  var aVariable: Int = 42
  aVariable += 1 // this maybe be done since it's var

  // code block
  val aCodeBlock: String = {
    println("I am doing something here")
    Thread sleep 1000

    "Done!"
  } // returns the last expression, in this case it's "Done"

  // types
  // Unit
  val theUnit = println("Hello, Scala") // returns the unit value = ()
  val theUnit2 = {
    println("Hello, Scala")

    ()
  }

  def aFunction(x: Int): String = (x + 1).stringify

  implicit class IntToStringOps(x: Int) {
    def stringify: String = x.toString
  }

  // recursion - TAIL recursion
  @tailrec
  def factorial(n: Int, acc: Int): Int = {
    if (n <= 0) acc
    else factorial(n - 1, acc * n)
  }

  // OOP

  class Animal

  class Dog extends Animal
  class Cat extends Animal

  val aDog: Animal = new Dog
  val aCat: Animal = new Cat

  trait Carnivore {
    def eat(animal: Animal): Unit
  }

  class Crocodile extends Animal with Carnivore {
    override def eat(animal: Animal): Unit = println(s"Crocodile is eating $animal")
  }

  val aCroc = new Crocodile
  aCroc eat aDog
  aCroc eat aCat

  val anonCarnivore = new Carnivore {
    override def eat(animal: Animal): Unit = println(s"Anon is eating $animal")
  }

  // generics, typed data structures
  abstract class MyList[+A]

  // companion objects
  object MyList {

  }

  // case classes
  case class Person(name: String, age: Int) // a LOT in this course!

  // Exceptions
  val aPotentialFailure = try {
    throw new RuntimeException("Boom")
  } catch {
    case e: RuntimeException => "I caught an exception!"
  } finally {
    // side effects
    println("some logs or finalizers which will always run")
  }

  // FP

  val incrementerVerbose: Function1[Int, Int] = new Function1[Int, Int] {
    override def apply(v1: Int): Int = v1 + 1
  }

  val incrementer: Int => Int = _ + 1

  val anonIncrementer = (x: Int) => x + 1

  // FP is all about working with functions as first-class
  // map is a Higher Order Function
  println(List(1, 2, 3).map(_ * 2).flatMap(i => List(i, i + 1)).filter(_ > 2))

  // for comprehensions

  val pairs: List[(Int, Char)] = for {
    num  <- List(1, 2, 3, 4, 5)
    char <- List('a', 'b', 'c', 'd', 'e')
  } yield (num, char)

  // same as
  val pairsVerbose: List[(Int, Char)] =
    List(1, 2, 3, 4, 5)
      .flatMap(num => List('a', 'b', 'c', 'd', 'e')
        .map(char => (num, char)))

  val anOption = Some(2)
  val aTry     = scala.util.Try(42 / 0)
  val anEither = Right("Yeap!")

  // pattern matching
  val unknown: Int  = 2

  val order: String = unknown match {
    case 1 => "First"
    case 2 => "Second"
    case _ => "Unknown"
  }

  val bob = Person("Bob", 22)

  val greeting = bob match {
    case Person(name, age) => s"Hi, my name is $name and I am $age years old"
  }

}
