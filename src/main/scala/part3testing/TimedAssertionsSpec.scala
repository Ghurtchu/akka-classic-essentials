package part3testing

import akka.actor.{Actor, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike

import scala.concurrent.duration._
import scala.util.Random

class TimedAssertionsSpec extends TestKit(ActorSystem("TimedAssertionSpec"))
  with ImplicitSender
  with AnyWordSpecLike
  with BeforeAndAfterAll {

  import TimedAssertionsSpec._

  override protected def afterAll(): Unit = TestKit.shutdownActorSystem(system)

  "A worker actor" should {

    val worker = system.actorOf(Props[WorkerActor])

    "reply with the meaning of life in a timely manner" in {
      within(500.millis, 1.second) { // from 500 millis to 1 sec
        worker ! "work"
        expectMsg(WorkResult(42))
      }
    }

    "reply with valid work at a reasonable cadence" in {
      within(1.second) {
        worker ! "workSequence"
        val results = receiveWhile[Int](max = 2.seconds, idle = 500.millis, messages = 10) {
          case WorkResult(res) => res
        }
        assert(results.sum > 5)
      }
    }
    
  }

}

object TimedAssertionsSpec {

  final case class WorkResult(result: Int)

  class WorkerActor extends Actor {
    override def receive: Receive = {
      case "work"         =>
        Thread sleep 500
        sender ! WorkResult(42)
      case "workSequence" =>
        val r = new Random()
        for (i <- 1 to 10) {
          Thread.sleep(r.nextInt(50)) // up to 50 milli secs
          sender ! WorkResult(1)
        }
    }
  }

}
