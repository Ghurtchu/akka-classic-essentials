package part2actors.logging

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}

object WithTrait extends scala.App {

  class ActorWithImplicitLogger extends Actor with ActorLogging {
    override def receive: Receive = {
      case msg => log.info(msg.toString)
    }
  }

  val system = ActorSystem("sys")

  val actor = system.actorOf(Props[ActorWithImplicitLogger])

  actor ! "let's go"
  actor ! true
  actor ! List(1, 2, 3)

}
