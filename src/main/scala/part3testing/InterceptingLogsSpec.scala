package part3testing

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.testkit.{EventFilter, ImplicitSender, TestKit}
import com.typesafe.config.ConfigFactory
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike
import part3testing.InterceptingLogsSpec.{Checkout, CheckoutActor}

class InterceptingLogsSpec extends TestKit(ActorSystem("InterceptingLogsSpec", ConfigFactory.load("interceptingLogMessages")))
  with ImplicitSender
  with AnyWordSpecLike
  with BeforeAndAfterAll {

  override protected def afterAll(): Unit = TestKit.shutdownActorSystem(system)

  val item       = "Rock the JVM Akka Course"
  val creditCard = "1234-1234-1234-1234"

  "A checkout flow" should {
    "correctly log the dispatch of an order" in {
      EventFilter.info(pattern = s"Order [0-9]+ for item $item has been dispatched.", occurrences = 1) intercept {
        // out test code
        val checkoutRef = system.actorOf(Props[CheckoutActor])
        checkoutRef ! Checkout(item, creditCard)
      }
    }
  }

}

object InterceptingLogsSpec {

  final case class Checkout(item: String, creditCard: String)
  final case class AuthorizeCard(creditCard: String)
  final case class DispatchOrder(item: String)
  case object PaymentAccepted
  case object PaymentDenied
  case object OrderConfirmed

  class CheckoutActor extends Actor {

    private val paymentManager = context.actorOf(Props[PaymentManager])
    private val fulfillmentManager = context.actorOf(Props[FulfillmentManager])

    override def receive: Receive = awaitingCheckout

    def awaitingCheckout: Receive = {
      case Checkout(item, creditCard) =>
        paymentManager ! AuthorizeCard(creditCard)
        context.become(pendingPayment(item)) // switch receive message handler to pendingPayment
    }

    def pendingPayment(item: String): Receive = {
      case PaymentAccepted =>
        fulfillmentManager ! DispatchOrder(item)
        context.become(pendingFulfillment(item)) // switch to pendingFulfillment
    }

    def pendingFulfillment(item: String): Receive = {
      case OrderConfirmed => context.become(awaitingCheckout) // switch to awaitingCheckout
    }

  }

  class PaymentManager extends Actor {
    override def receive: Receive = {
      case AuthorizeCard(card) => sender ! (if (card.startsWith("0")) PaymentDenied else PaymentAccepted)
    }
  }

  class FulfillmentManager extends Actor with ActorLogging {

    var orderId: Int = 43

    override def receive: Receive = {
      case DispatchOrder(item) =>
        orderId += 1
        log.info(s"Order $orderId for item $item has been dispatched.")
        sender ! OrderConfirmed
    }
  }

}
