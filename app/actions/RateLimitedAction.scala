package actions


import com.google.common.util.concurrent.RateLimiter
import play.api.mvc._
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

/**
 * @Projectname: playTest01
 * @Filename: ActionBuilder
 * @Author: Wang Le
 * @Data:2024/6/18 14:45
 * @Description:
 *
 */
class RateLimitedAction @Inject()(parser: BodyParsers.Default)(implicit ec: ExecutionContext, config: play.api.Configuration)
  extends ActionBuilderImpl(parser) {

  // 从配置文件中读取限流值
  private val requestsPerSecond = config.get[Int]("rateLimit.requestsPerSecond")
  private val rateLimiter = RateLimiter.create(requestsPerSecond)

  override def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]): Future[Result] = {
    if (rateLimiter.tryAcquire()) {
      block(request)
    } else {
      Future.successful(Results.TooManyRequests("Too many requests"))
    }
  }
}
