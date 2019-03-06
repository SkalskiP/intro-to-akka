package part2actors

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

object IntroAkkaConfig extends App {

  class SimpleLoggingActor extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
    }
  }

  /*/
  config file
   */

  val defaultConfigFailSystem = ActorSystem("DefaultConfigFailDemo")
  val defaultConfigActor = defaultConfigFailSystem.actorOf(Props[SimpleLoggingActor])

  defaultConfigActor ! "Remember me"

  /*/
  separate config in the same file
   */

  val specialConfig = ConfigFactory.load().getConfig("mySpecialConfig")
  val specialConfigFailSystem = ActorSystem("SpecialConfigFailDemo", specialConfig)
  val specialConfigActor = specialConfigFailSystem.actorOf(Props[SimpleLoggingActor])

  specialConfigActor ! "Remember me, I'm special"

  /*
  separate config in another file
   */

  val separateConfig = ConfigFactory.load("secretFolder/secretConfiguration.conf")
  println(separateConfig.getString("akka.loglevel"))
}
