package part3testing

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike
import TestProbeSpec._

class TestProbeSpec extends TestKit(ActorSystem("TestProbeSpec"))
  with ImplicitSender
  with AnyWordSpecLike
  with BeforeAndAfterAll {

  override protected def afterAll(): Unit = TestKit.shutdownActorSystem(system)

  "A master actor" should {
    "register a slave" in {
      val master = system.actorOf(Props[Master])
      val slave: TestProbe = TestProbe("slave") // special actor with some assertion capabilities
      master ! Register(slave.ref) // ActorRef of slave
      expectMsg(RegistrationAck)
    }
    "send the work to the slave actor" in {
      val master = system.actorOf(Props[Master]) // craete new master here because it's stateful
      val slave = TestProbe("slave")
      master ! Register(slave.ref)
      expectMsg(RegistrationAck)

      val workload = "I Love Akka"
      master ! Work(workload)
      // test the interaction between master and the slave actor, slave actor is fictitious with the help of TestProbe
      slave.expectMsg(SlaveWork(workload, testActor))
      slave.reply(WorkCompleted(3, testActor))
      expectMsg(Report(3))
    }
    "aggregate data correctly" in {
      val master = system.actorOf(Props[Master])
      val slave: TestProbe = TestProbe("slave") // special actor with some assertion capabilities
      master ! Register(slave.ref) // ActorRef of slave
      expectMsg(RegistrationAck)

      val work = "I Love Akka"
      master ! Work(work)
      master ! Work(work)

      slave.receiveWhile() {
        case SlaveWork(`work`, `testActor`) => slave.reply(WorkCompleted(3, testActor))
      }

      // In the meantime I don't have a slave actor
      expectMsg(Report(3))
      expectMsg(Report(6))

    }

  }


}

object TestProbeSpec {

  /**
   * word counting actor hierarchy master-slave
   */

  // send some work to the master
  // - master sends the slave the piece of work
  // - slave processes the work and replies to master
  // - master aggregates the result
  // - master sends the total word count to the original requester

  final case class Register(actorRef: ActorRef)
  final case class SlaveWork(text: String, originalRequester: ActorRef)
  final case class WorkCompleted(count: Int, originalRequester: ActorRef)
  final case class Work(text: String)
  final case class Report(totalWordCount: Int)
  case object RegistrationAck

  class Master extends Actor {

    override def receive: Receive = {
      case Register(slaveRef) =>
        sender ! RegistrationAck
        context.become(online(slaveRef, 0))
      case _                  => // ignored
    }

    def online(ref: ActorRef, totalWordCount: Int): Receive = {
      case Work(text) => ref ! SlaveWork(text, sender)
      case WorkCompleted(count, requester) =>
        val newTotalWordCount = totalWordCount + count
        requester ! Report(newTotalWordCount)
        context.become(online(ref, newTotalWordCount)) // update totalWordCount
    }

  }


}
