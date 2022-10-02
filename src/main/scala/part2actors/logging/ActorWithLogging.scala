package part2actors.logging

import akka.actor.{Actor, ActorSystem, Props}
import akka.event.Logging

object ActorWithLogging extends scala.App {

  class ActorWithExplicitLogger extends Actor {
    val logger = Logging(context.system, this)

    override def receive: Receive = {
      case message => logger.info(message.toString)
    }
  }

  val system = ActorSystem("ActorSystem")

  val actor = system.actorOf(Props[ActorWithExplicitLogger])

  actor ! 1
  actor ! "str"
  actor ! true

}
