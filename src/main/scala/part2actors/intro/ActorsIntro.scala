package part2actors.intro

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object ActorsIntro extends scala.App {

  // part1 - Actor Systems
  val actorSystem = ActorSystem("firstActorSystem")
  println(actorSystem.name)

  // part2 - creating Actors
  // actors are like humans talking to each other

  // Actors are uniquely identified, like humans are with their social ID-s
  // Actor messages are processed asynchronously
  // Each actor may respond differently
  // Actors are really encapsulated

  // Our first actor counts word

  class WordCountActor extends Actor {

    // internal data
    private var totalWords: Int = 0

    // behaviour
    // type Receive = PartialFunction[Any, Unit]
    override def receive: PartialFunction[Any, Unit] = {
      case msg: String => {
        println(s"[word counter] I have received message: $msg")
        totalWords += msg.split(" ").length
      }
      case _ => println(s"[word counter] I can not understand, unknown message.")
    }

  }

  // part 3 - instantiate our actor

  val wordCounter: ActorRef = actorSystem.actorOf(Props[WordCountActor], "wordCounter")
  val anotherWordCounter: ActorRef = actorSystem.actorOf(Props[WordCountActor], "anotherWordCounter")

  // part 4 - communicate!
  // sending the message is completely async
  wordCounter ! "I am learning Akka and it's pretty damn cool!" // "tell"

  // message sending is completely async
  anotherWordCounter ! "A different message"

  object Person {
    def props(name: String): Props = Props(new Person(name))
  }

  class Person(name: String) extends Actor {
    override def receive: Receive = {
      case "hi" => println(s"Hi, my name is $name")
      case _ =>
    }
  }

  val personActor = actorSystem.actorOf(Person.props("Niko"), "personActor")

  personActor ! "hi"

}
