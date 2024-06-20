package akka_learning

import akka.actor.{Actor, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

import scala.io.StdIn

/**
 * @Projectname: 2024_training
 * @Filename: Human
 * @Author: Wang Le
 * @Data:2024/6/7 12:57
 * @Description: TODO
 *
 */
class  Human extends Actor{
  override def receive: Receive = {
    case msg =>{
      println(s"siri : ${msg}")
      println("请输入和siri说的话：")
      val line = StdIn.readLine()
      sender()!line
    }
  }

  override def preStart(): Unit = {
    //初始化就执行
    println("请输入和siri说的话：")
    val line = StdIn.readLine()
    val proxy = context.actorSelection("akka.tcp://CUG@localhost:6666/user/siri")
    proxy ! line
  }
}
object Human{
  def main(args: Array[String]): Unit = {
    val conf =
      """
        |akka.actor.provider = akka.remote.RemoteActorRefProvider
        |akka.remote.netty.tcp.hostname = localhost
        |akka.remote.netty.tcp.port = 6066
        |""".stripMargin
    val config = ConfigFactory.parseString(conf)
    val actorSystem = ActorSystem.create("CUG2", config)
    actorSystem.actorOf(Props(new Human),"cs")
  }
}
