package actors

import akka.actor.{Actor, ActorRef}
import play.api.Logger.logger
import scala.concurrent.blocking
import scala.concurrent.duration.{DurationInt, FiniteDuration}
import scala.math.sqrt
import scala.util.Random

/**
 * @Projectname: playTest01
 * @Filename: SquareRootActor
 * @Author: Wang Le
 * @Data:2024/6/18 20:07
 * @Description:
 */
class SquareRootActor() extends Actor {

  import context.dispatcher

  override def receive: Receive = {
    case (patten: String, num: Int) =>
      patten match {
        case "highLevel" =>
          val delay = (100 + Random.nextInt(151)).milliseconds
          val originalSender: ActorRef = sender()
          // 使用 scheduleOnce 模拟延迟操作（异步）
          (context.system.scheduler scheduleOnce delay) {
            val res = handleSquare("[High]", delay, num)
            originalSender ! res
          }
        case "lowLevel" =>
          blocking {
            val delay = Random.nextInt(151) + 100
            // 使用 Thread.sleep 模拟延迟操作（同步）
            Thread.sleep(delay)
            val res = handleSquare("[Low]", delay.milliseconds, num)
            sender() ! res
          }
      }
    case _ =>
      val msg = "参数错误，已退出"
      logger.error(msg)
  }

  private def handleSquare(s: String,delay: FiniteDuration, num: Int) = {
    if (delay < 100.milliseconds) {
      val msg = s"$s delay = $delay,Implementation Errors"
      logger.error(msg)
      msg
    } else if (delay <= 200.milliseconds) {
      val value = BigDecimal(sqrt(num.toDouble)).setScale(2, BigDecimal.RoundingMode.HALF_UP)
      val msg = s"$s delay = $delay, $num 's squareRoot = $value"
      logger.info(msg)
      value
    } else {
      val msg = s"$s delay = $delay, Time out"
      logger.error(msg)
      msg
    }
  }
}

object SquareActor {
}