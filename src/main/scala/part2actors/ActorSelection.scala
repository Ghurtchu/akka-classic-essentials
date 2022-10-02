package part2actors

import akka.actor.{Actor, ActorRef, ActorSelection, ActorSystem, Props}

object ActorSelection extends scala.App {

  class SimpleActor extends Actor {
    override def receive: Receive = {
      case _ => println("received msg")
    }
  }

  val system = ActorSystem("sys")

  val actor: ActorRef = system.actorOf(Props[SimpleActor], "simpleActor")

  val selectedActor: ActorSelection = system.actorSelection("simpleActor")

  actor ! "bang!"

  println(selectedActor)
  println(actor)

}
