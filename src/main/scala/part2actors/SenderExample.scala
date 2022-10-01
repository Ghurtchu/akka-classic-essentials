package part2actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object SenderExample extends scala.App {

  class SimpleActor extends Actor {

    // context.sender() is the actor reference who last sent the message to self/"this" actor

    override def receive: Receive = {
      case "Hi"         =>
        println(self + " received message")
        println("self = " + self)
        println("sender = " + sender())
        println("actor1 send message to actor2")
      case SayHiTo(ref) =>
        println(self + " received message")
        println("self = " + self)
        println("sender = " + sender())
        println("deadLetters sent message to actor1")
        ref ! "Hi"
    }
  }

  case class SayHiTo(ref: ActorRef)

  val system = ActorSystem("system")

  val actor1 = system.actorOf(Props[SimpleActor], "actor1")
  val actor2 = system.actorOf(Props[SimpleActor], "actor2")

  actor1 ! SayHiTo(actor2)


}
