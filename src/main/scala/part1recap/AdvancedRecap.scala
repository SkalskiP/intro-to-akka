package part1recap

object AdvancedRecap extends App {

  // partial functions
  val partialFunction: PartialFunction[Int, Int] = {
    case 1 => 42
    case 2 => 65
    case 5 => 999
  }

  val pf = (x: Int) => match {
    case 1 => 42
    case 2 => 65
    case 5 => 999
  }

  val function: (Int => Int) = partialFunction

  val modifiedList = List(1,2,3).map {
    case 1 => 42
    case _ => 0
  }

  //lifting
  val lifted = partialFunction.lift // total function Int => Option[Int]
  lifted(2) // Some(65)
  lifted(5000) // None

  //orElse
  val pfChain = partialFunction.orElse[Int, Int] {
    case 60 => 9000
  }

  pfChain(5) // 999 per partialFunction
  pfChain(60) // 9000
  pfChain(457) // throw a MatchError

  // type aliases
  type ReceiveFunction = PartialFunction[Any, Unit]

  def receive: ReceiveFunction = {
    case 1 => println("hello")
    case _ => println("confused...")
  }

  // implicits
  implicit val timeout = 3000
  def setTimeout(f: () => Unit)(implicit timeout: Int) = f()
  setTimeout(() => println("timeout")) //extra parameter list omitted
}
