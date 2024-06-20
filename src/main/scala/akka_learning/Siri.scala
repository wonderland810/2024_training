package akka_learning

import akka.actor.{Actor, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

/**
 * @Projectname: 2024_training
 * @Filename: Test
 * @Author: Wang Le
 * @Data:2024/6/6 21:31
 * @Description: TODO
 *
 */

class Siri extends Actor{
  override def receive: Receive = {
    case "hello" => sender() ! "hello"
    case "eat?" => sender() ! "yes"
    case "eat what?" => sender() ! "rice"
    case "teast good?" => sender() ! "very good"
    case "bye" => sender() ! "bye"
    case "learn bigdata?" => sender() ! "ok fine"
    case "come to CUG" => sender() ! "ok fine"
    case _ => sender() ! "what???"
  }
}

object Siri {
  def main(args: Array[String]): Unit = {
    val conf =
      """
        |akka.actor.provider = akka.remote.RemoteActorRefProvider
        |akka.remote.netty.tcp.hostname = localhost
        |akka.remote.netty.tcp.port = 6666
        |""".stripMargin
    val config = ConfigFactory.parseString(conf)
    val actorSystem = ActorSystem.create("CUG",config)
    val actor = actorSystem.actorOf(Props(new Siri),"siri")
  }
}
