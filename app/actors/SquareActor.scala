package actors

import akka.actor.{Actor, ActorRef}
import play.api.i18n.Lang.logger

import scala.concurrent.blocking
import scala.concurrent.duration.{DurationInt, FiniteDuration}
import scala.math.sqrt
import scala.util.Random


/**
 * @Projectname: playTest01
 * @Filename: SquareActor
 * @Author: Wang Le
 * @Data:2024/6/18 20:07
 * @Description: TODO
 *
 */
class SquareActor() extends Actor {

  import context.dispatcher

  override def receive: Receive = {
    case (patten: String, num: Int) =>
      patten match {
        case "highLevel" =>
          val delay = Random.nextInt(251).milliseconds
          val originalSender: ActorRef = sender()
          //          println("highLevel")
          // 使用 scheduleOnce 模拟延迟操作（异步）
          (context.system.scheduler scheduleOnce delay) {
            val res = handleSquare(delay, num)
            originalSender ! res
          }
        case "lowLevel" =>
          blocking {
            val delay = Random.nextInt(251)
            // 使用 Thread.sleep 模拟延迟操作（同步）
            Thread.sleep(delay)
            val res = handleSquare(delay.milliseconds, num)
            sender() ! res
          }
      }
    case _ =>
      val msg = "参数错误，已退出"
      logger.error(msg)
  }

  private def handleSquare(delay: FiniteDuration, num: Int) = {
    if (delay < 100.milliseconds) {
      val msg = "times consuming: " + delay + ",实现错误"
      logger.error(msg)
      msg
    } else if (delay <= 200.milliseconds) {
      val value = BigDecimal(sqrt(num.toDouble)).setScale(2, BigDecimal.RoundingMode.HALF_UP)
      val msg = "times consuming: " + delay + ", 结果：" + value
      logger.info(msg)
      value
    } else {
      val msg = "times consuming: " + delay + ",超时"
      logger.error(msg)
      msg
    }
  }

}

object SquareActor {

}
