package part1recap

object ThreadModelLimitations extends scala.App {

  /**
   * Daniel's rants
   */

  // OOP encapsulation is only valid in the SINGLE THREADED MODEL

  class BankAccount(private var amount: Int) {
    override def toString: String = "" + amount

    def withdraw(amount: Int): Unit = this.synchronized( {
      this.amount -= amount
    })

    def deposit(amount: Int): Unit = this.synchronized {
      this.amount += amount
    }

    def getAmount: Int = amount
  }

  val acc = new BankAccount(20_000)

  lazy val d = for (_ <- 1 to 10000) {
    new Thread(() => acc.withdraw(1)).start()
  }

  lazy val t = for (_ <- 1 to 10000) {
    new Thread(() => acc.deposit(1)).start()
  }

  println(acc.getAmount)

  // OOP encapsulation is broken in a multithreaded env

  // synchronization! Locks to the rescue

  // locks introduce more problems: deadlocks, livelocks etc..

  // We need a data structure:
  // - fully encapsulated
  // - no locks

  // Daniel's rant #2 - delegating something toa thread is a PAIN. (not the executor service)

  // you have a running thread and you want to pass a runnable to that thread.

  var task: Runnable = _

  val runningThread: Thread = new Thread(() => {
    while (true) {
      while (task == null) {
        runningThread.synchronized {
          println("[background] waiting for a task...")
          runningThread.wait()
        }
      }
      task.synchronized {
        println("[background] I have a task")
        task.run()
        task = null
      }
    }
  })

  def delegateToBackgroundTread(r: Runnable): Unit = {
    if (task == null) task = r

    runningThread.synchronized {
      runningThread.notify()
    }
  }

  runningThread.start()

  Thread sleep 500

  delegateToBackgroundTread(() => println(42))

  Thread sleep 500

  delegateToBackgroundTread(() => println("This should run in the background"))

  // Dainel's 3rd rant: tracing and dealing with errors in a multithreaded env is a PITN.

  // 1M numbers in between 10 threads

  import scala.concurrent.ExecutionContext.Implicits.global
  import scala.concurrent.Future


}
