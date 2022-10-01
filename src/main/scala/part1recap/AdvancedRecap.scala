package part1recap

object AdvancedRecap extends scala.App {

  // partial functions - handle only subset of input domain
  // only defined for x = {1, 2, 3}
  val partialFunc: PartialFunction[Int, Int] = {
    case 1 => 2
    case 2 => 3
    case 3 => 4
  }

  // equivalent partial function
  val pf2 = (x: Int) => x match {
    case 1 => 2
    case 2 => 3
    case 3 => 4
  }

  // again, equivalent
  val pf3: Int => Int = {
    case 1 => 2
    case 2 => 3
    case 3 => 4
  }

  // map with partial func
  val modifiedList = List(1, 2, 3).map {
    case 1 => 42
    case _ => 0
  }

  // lifting
  val lifted: Int => Option[Int] =
    partialFunc.lift // lifts it to total function with type: Int => Option[Int]

  lifted(1) // Some(2)
  lifted(10_000) // None

  // or Else
  val pfChain: PartialFunction[Int, Int] = partialFunc.orElse[Int, Int] {
    case 60 => 9000
  }

  pfChain(1) // 1 (from first)
  pfChain(60) // 9000 (from second)
  pfChain(456) // will throw a MatchError

  // type aliases
  type ReceiveFunction = PartialFunction[Any, Unit]

  def receive: ReceiveFunction = {
    case true   => println("hello")
    case "what" => ()
    case _      => println("idk what to do")
  }

  // implicits
  case class Timeout(timeout: Int)
  implicit val timeout: Timeout = Timeout(3000)

  def setTimeOut(f: () => Unit)(implicit timeout: Timeout) = f()

  setTimeOut(() => println("timeout")) // compiler injects Timeout instance here itself

  // implicit conversions

  case class Person(name: String) {
    def greet = s"Hi! my name is $name"
  }

  implicit def strToPerson(str: String): Person = Person(str)

  "peter".greet // string is implicitly converted to Person instance

  // 2) implicit classes
  implicit class Dog(name: String) {
    def bark = println("bark")
  }

  "Lassie".bark // same as (new Dog("Lassie")).bark

  // implicits must be organized properly to reduce the misunderstanding

  // implicit in local scope
  implicit val inverseOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)
  println(List(1, 2, 3).sorted) // List(3, 2, 1)

  import scala.concurrent.Future
  import scala.concurrent.ExecutionContext.Implicits.global

  // imported scope
  val future: Future[String] = Future {
    Thread sleep 2000

    "Hello from Future!"
  }

  // companion objects of the types included in the call
  object Person {
    implicit val personOrdering: Ordering[Person] =
      Ordering.fromLessThan((a, b) => a.name.compareTo(b.name) < 0)
  }

  // will take the implicit ordering defined in Person companion object
  List(Person("Bob"), Person("Alice")).sorted

}
