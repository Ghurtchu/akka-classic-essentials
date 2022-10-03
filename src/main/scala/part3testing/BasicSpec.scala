package part3testing

import akka.actor.{Actor, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike
import part3testing.BasicSpec.{BlackHole, LabTestActor, SimpleActor}

import scala.concurrent.duration.DurationInt
import scala.util.Random
// when we run this suite it will instantiate BasicSpec ActorSystem
class BasicSpec extends TestKit(ActorSystem("BasicSpec"))
  with ImplicitSender
  with AnyWordSpecLike
  with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    // will be run after all tests are run and will shut down the actor system
    TestKit.shutdownActorSystem(system)
  }

  lazy val testStructure = "The thing being tested" should {
    "do this" in {
      // testing scenario here
    }
    "do another thing" in {
      // testing scenario here
    }
  }

  "A Simple Actor" should {
    "send back the same message" in {
      val echoActor = system.actorOf(Props[SimpleActor])
      val msg = "hello, test"
      echoActor ! msg

      expectMsg(msg)
    }
  }

  "A Blackhole Actor" should {
    "send back some message" in {
      val echoActor = system.actorOf(Props[BlackHole])
      val msg = "hello, test"
      echoActor ! msg

      expectNoMessage(1.second)
    }
  }

  testActor // actor which is sender for testing actor behaviours

  "A LabTestActor" should {
    val labTestActor = system.actorOf(Props[LabTestActor])
    "turn a `str` into `STR`" in {
      labTestActor ! "str"
      val reply = expectMsgType[String]
      assert(reply == "STR")
      assert(reply.length == 3)
    }
    "turn `A` into `A`" in {
     labTestActor ! "A"
     expectMsg("A")
    }
    "respond with either `hi` or `hello`" in {
      labTestActor ! "greeting"
      expectMsgAnyOf("hello", "hi")
    }
    "respond with Scala and Akka on `favoriteTech`" in {
       labTestActor ! "favoriteTech"
       expectMsgAllOf("Scala", "Akka")
    }
    "respond with cool tech in a different way" in {
      labTestActor ! "favoriteTech"
      val messages = receiveN(2) // Seq[Any]
      assert(messages.head == "Scala")
      assert(messages.tail.head == "Akka")
      // free to do more complicated assertions
    }
    "respond with cool tech in a fancy way" in {
     labTestActor ! "favoriteTech"
     expectMsgPF() {
       case "Scala" =>
       case "Akka"  =>
     }
    }

  }

}

object BasicSpec {

  // echo actor
  class SimpleActor extends Actor {
    override def receive: Receive = {
      case msg => sender ! msg
    }
  }

  class BlackHole extends Actor {
    override def receive: Receive = Actor.emptyBehavior
  }

  class LabTestActor extends Actor {

    private final val rand = new Random()

    override def receive: Receive = {
      case "greeting"     => sender ! (if (rand.nextBoolean()) "hi" else "hello")
      case "favoriteTech" => sender ! "Scala"; sender ! "Akka"
      case msg: String    => sender ! msg.toUpperCase
    }
  }

}
