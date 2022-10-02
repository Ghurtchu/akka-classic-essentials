package part2actors.intro

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object Context extends scala.App {

  final case class Question(content: String, ref: ActorRef)
  final case class Message(content: String, ref: ActorRef)
  final case class EyeContact(ref: ActorRef)

  class Person extends Actor {
    override def receive: Receive = {
      case Question(q, ref) => println(s"${self.path.name} asking a question to ${ref.path.name}")
      case Message(m, ref)  => println(s"${self.path.name} sending a message to ${ref.path.name}")
      case EyeContact(ref)  => println(s"${self.path.name} looking at ${ref.path.name}")
    }
  }

  val system = ActorSystem("System")

  val p1 = system.actorOf(Props[Person], "Nika")
  val p2 = system.actorOf(Props[Person], "Laliko")

  while (true) {
    p1 ! EyeContact(p2)
    Thread sleep 250
    p2 ! EyeContact(p1)
    val question = Question("Asking a question", p2)
    p1 ! question
    Thread sleep 250
    val msg = Message("Sending a message", p1)
    p2 ! msg
    Thread sleep 250
  }



}
