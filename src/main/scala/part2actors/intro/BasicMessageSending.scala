package part2actors.intro

import akka.actor.{Actor, ActorSystem, Props}

object BasicMessageSending extends scala.App {

  final case class WordCountTask(content: String)
  final case class WordCountReply(count: Int)

  class TaskInitializer extends Actor {
    override def receive: Receive = {

      case s: String =>
        val actor = context.actorOf(Props[WordCountActor])
        actor ! WordCountTask(s)

      case WordCountReply(count) => println(count)

    }
  }

  class WordCountActor extends Actor {
    override def receive: Receive = {
      case WordCountTask(content) => sender ! WordCountReply(content.split(" ").length)
    }
  }

  val actor = ActorSystem("System")

  val master = actor.actorOf(Props[TaskInitializer])

  while (true) {
    val txt = "word " * scala.util.Random.nextInt(100)
    master ! txt
    Thread sleep 500
  }

}
