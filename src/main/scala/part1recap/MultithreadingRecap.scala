package part1recap

import scala.concurrent.Future
import scala.util.{Failure, Success}

object MultithreadingRecap extends App {
  // creating threads on the JVM

//  val aThread = new Thread(new Runnable {
//    override def run(): Unit = println("I'm running in parallel")
//  })

  val aThread = new Thread(() => println("I'm running in parallel"))
  aThread.start()
  aThread.join()

  val threadHello = new Thread(() => (1 to 1000).foreach(_ => println("hello")))
  val threadGoodbye = new Thread(() => (1 to 1000).foreach(_ => println("goodbye")))
  threadHello.start()
  threadGoodbye.start()

  // different runs produce different results!

  class BankAccount(@volatile private var amount: Int) {
    override def toString: String = "" + amount

    def withdraw(money: Int) = this.amount -= money

    def safeWithdraw(money: Int) = this.synchronized {
      this.amount -= money
    }

    // Scala Futures
    import scala.concurrent.ExecutionContext.Implicits.global
    val future = Future {
      // long computation - on a different thread
      42
    }

    // callbacks
    future.onComplete {
      case Success(42) => println("I found the meaning of life")
      case Failure(_) => println("Something happened with the meaning of life")

      val aProcessedFuture = future.map(_ + 1) // Future with 43
      val aFlatFuture = future.flatMap {
        value => Future(value + 2)
      } // Future with 44

      val filteredFuture = future.filter(_ % 2 == 0) // NoSuchElementException

      // for comprehensions
      val aNonsenseFuture = for {
        meaningOfLife <- future
        filteredMeaning <- filteredFuture
      } yield meaningOfLife + filteredMeaning

      // andThen, recover/recoverWith

    }
  }
}