package part3testing

import akka.actor.{Actor, ActorSystem, Props}
import akka.testkit.TestActorRef
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike
import part3testing.SynchronousTestingSpec._

class SynchronousTestingSpec extends AnyWordSpecLike with BeforeAndAfterAll {

  implicit val sys: ActorSystem = ActorSystem("SynchronousTestingSpec")

  override protected def afterAll(): Unit = sys.terminate()

  "A Counter" should {
    "synchronously increase its counter" in {
      val counter = TestActorRef[Counter](Props[Counter])
      counter ! Inc // counter has ALREADY received the message, it's synchronous, it's happening in the calling thread
      assert(counter.underlyingActor.count == 1)
    }
  }

}

object SynchronousTestingSpec {
  case object Inc
  case object Read

  class Counter extends Actor {
    var count = 0

    override def receive: Receive = {
      case Int  => count += 1
      case Read => sender ! count
    }
  }
}
