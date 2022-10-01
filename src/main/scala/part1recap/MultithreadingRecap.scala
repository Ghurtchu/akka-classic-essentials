package part1recap

import scala.util.{Failure, Success}

object MultithreadingRecap extends scala.App {

  // creating threads on the JVM
  // new Thread and then the Runnable instance

  val aThread = new Thread(() => println("let's gooooooooo"))
  aThread.start()
  aThread.join()

  // threads are unpredictable
  // printed ordering of "hello" and "goodbye" ar mixed
  // each new run will produce a totally new view of print ordering
  val threadHello   = new Thread(() => (1 to 1000).foreach(_ => println("hello")))
  val threadGoodbye = new Thread(() => (1 to 1000).foreach(_ => println("goodbye")))

  threadHello.start()
  threadGoodbye.start()

  // volatile locks the member for read/write at the same time
  class BankAccount(@volatile private var amount: Int) {

    override def toString: String = s"$amount"

    // this method is not thread safe
    // may run into race conditions
    def withdraw(money: Int) = this.amount -= money

    // synchronization prevents two threads to read/write simultaneously
    def safeWithdraw(money: Int) = this.synchronized {
      this.amount -= money
    }

  }

  /**
   * BA(1000)
   *
   * T1 -> withdraw 1000
   * T2 -> withdraw 2000
   *
   * T1 -> this.amount = this.amount - ///
   * T2 -> this.amount = this.amount - 2000 = 8000
   *
   * T1 -> - 1000 = 9000
   * 9000 is not a correct answer, it's a race condition
   */

  // inter-thread communication on the JVM
  // wait - notify mechanism

  // Scala Futures (IMPORTANT!!!)
  import scala.concurrent.Future
  import scala.concurrent.ExecutionContext.Implicits.global

  val future = Future {
    // long computation - on a different thread
    Thread sleep 2000

    42
  }

  // callbacks
  future.onComplete {
    case Success(value) => println(value)
    case Failure(exc)   => println(s"failed due to ${exc.getMessage}")
  }

  val aProcessedFuture = future.map(_ + 1) // Future[Int] = 42

  val aFlatFuture: Future[Int] = for {
    value <- future
    v2    <- Future(value + 48)
  } yield v2

  val filteredFuture = future.filter(_ % 2 == 0) // succeeds or fails with NoSuchElementException

  val aNonsenseFuture: Future[Int] = for {
    mol         <- future
    filteredMol <- filteredFuture
  } yield (mol + filteredMol)

  // andThen & recovering like recover/recoverWith

  // Promises <- writeable futures

  Thread.currentThread().join() // wait till it finished

}
