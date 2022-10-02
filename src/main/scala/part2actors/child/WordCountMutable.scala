package part2actors.child

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

// parallelism

object WordCountMutable extends scala.App {

  import WordCounterMaster._

  // Distributed word counting


  // will create word count workers
  class WordCounterMaster() extends Actor {

    var childrenCount: Int = 0
    var wordCount: Int = 0

    override def receive: Receive = {

      case Initialize(n) =>
        childrenCount = n
        val workers = for (i <- 0 to n) yield context.actorOf(Props[WordCounterWorker], s"worker_$i")
        workers.foreach(_ ! WordCountTask("It has four words."))

      case WordCountReply(count) =>
        if (childrenCount == 0) {
          println(s"count = $wordCount")
          self ! Aggregated(wordCount)
        }
        else {
          childrenCount -= 1
          wordCount += count
        }

      case Aggregated(count) => println(s"final result $count"); context.system.terminate()

    }
  }

  object WordCounterMaster {
    final case class Initialize(nChildren: Int)
    final case class WordCountTask(text: String)
    final case class WordCountReply(count: Int)
    final case class Aggregated(count: Int)
  }

  // will count words
  class WordCounterWorker extends Actor {
    override def receive: Receive = {
      case WordCountTask(text) => sender ! WordCountReply(text.split(" ").length)
    }
  }

  /**
   * create WordCounterMaster
   * send Initialize(10) to wordCounterMaster
   * send "Akka is awesome" to wordCounterMaster
   * wordCounterMaster will send WordCountTask("Akka is awesome") to one of its' children
   * child replies with WordCountReply(3) to the master
   * master replies with the 3 to the initial sender()
   */

  // flow: requester -> master -> children ->
  //       requester <- master <-          <-

  val system = ActorSystem("ActorSystem")

  val master = system.actorOf(Props[WordCounterMaster], "masterActor")

  master ! Initialize(10)

}
