package controllers

import actions.RateLimitedAction
import actors.SquareActor
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.pattern.{AskTimeoutException, ask}
import akka.util.Timeout
import play.api.mvc._
import javax.inject._
import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration.DurationInt

/**
 * @Projectname: playTest01
 * @Filename: Mycontroller
 * @Author: Wang Le
 * @Data:2024/6/17 15:59
 * @Description: TODO
 *
 */
@Singleton
class HomeController @Inject()(rateLimitedAction: RateLimitedAction, cc: ControllerComponents, actorSystem: ActorSystem) extends AbstractController(cc) {

  //超时时长：5s
  implicit val timeout: Timeout = 5.seconds

  // 使用自定义线程池
  implicit private val customExecutionContext: ExecutionContextExecutor = actorSystem.dispatchers.lookup("threadPools.custom-thread-pool")

  //接收controller层数据的actor，模拟高延迟处理，然后返回结果(平方根)
  val squareActor: ActorRef = actorSystem.actorOf(Props[SquareActor](), "squareActor")

  //[高级]用其他异步逻辑模拟高延迟
  def high(num: Integer): Action[AnyContent] = rateLimitedAction.async { request =>
    (squareActor ? ("highLevel", num)).map { result =>
      Ok(result.toString)
    }.recover {
      //自定义超时逻辑，防止actor超时太长，不能及时退出
      case _: AskTimeoutException =>
        RequestTimeout("The request timed out. Please try again later.")
    }
  }

  //[低级]用sleep模拟高延迟处理
  def low(num: Integer): Action[AnyContent] = rateLimitedAction.async { request =>
    (squareActor ? ("lowLevel", num)).map { result =>
      Ok(result.toString)
    }.recover {
      case _: AskTimeoutException =>
        RequestTimeout("The request timed out. Please try again later.")
    }
  }
}
