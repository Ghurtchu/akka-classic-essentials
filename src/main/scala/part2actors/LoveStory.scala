package part2actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import part2actors.LoveStory.Person.Question

object LoveStory extends scala.App {

  class Person(val name: String) extends Actor {

    import Person._

    val rand = scala.util.Random

    override def receive: Receive = {
      case Question(q, ref) =>
        Thread sleep 250
        println(s"${this.name} asks: `$q`")
        Thread sleep 250
        ref ! Answer(if (rand.nextBoolean()) "yes" else "no", self)
      case Answer(a, ref)   =>
        Thread sleep 250
        println(s"${this.name} answers: `$a`")
        Thread sleep 250
        self ! Question("Do you love me?", ref)
    }
  }

  object Person {
    final case class Question(question: String, ref: ActorRef)
    final case class Answer(answer: String, ref: ActorRef)
  }

  val system = ActorSystem("actorSystem")

  val nika: ActorRef   = system.actorOf(Props(new Person("Nika")))
  val laliko: ActorRef = system.actorOf(Props(new Person("Laliko")))

  nika ! Question("Do you love me?", laliko)


}
