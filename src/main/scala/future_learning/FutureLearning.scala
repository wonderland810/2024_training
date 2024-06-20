package future_learning

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import scala.util.Success

/**
 * @Projectname: 2024_training
 * @Filename: FutureLearnning
 * @Author: Wang Le
 * @Data:2024/6/7 13:52
 * @Description: TODO
 *
 */
object FutureLearning {
  // 假设这是我们用来格式化字符串的函数
  def stringPretty(str: String): String = {
    // 模拟一些复杂的格式化工作
    Thread.sleep(100)  // 模拟耗时操作
    str.trim.toUpperCase
  }

  def main(args: Array[String]): Unit = {
    val fut = Future {
      Thread.sleep(1000)
      1 + 1
    }
    fut onComplete {
      case Success(r) => println(s"the result is ${r}")
      case _ => println("some Exception")
    }
    println("I am working")
    Thread.sleep(2000)

    println("=====================================")
    val strings = List("  hello  ", "  world  ", "  scala  ", "  concurrency  ")


    // 单线程处理
    val singleThreadStart = System.nanoTime()
    val singleThreadResult: List[String] = strings.map(stringPretty)
    val singleThreadEnd = System.nanoTime()
    val singleThreadDuration = (singleThreadEnd - singleThreadStart) / 1e6 // 毫秒
    println(s"Single-threaded processing took $singleThreadDuration ms")

    // 使用Future并行处理字符串列表
    val multiThreadStart = System.nanoTime()
    val formattedFutures: List[Future[String]] = strings.map { str =>
      Future {
        stringPretty(str)
      }
    }

    // 等待所有Future完成并获取结果
    val formattedStrings: List[String] = Await.result(Future.sequence(formattedFutures), 10.seconds)
    val multiThreadEnd = System.nanoTime()
    val multiThreadDuration = (multiThreadEnd - multiThreadStart) / 1e6 // 毫秒
    println(s"Multi-threaded processing took $multiThreadDuration ms")
    // 打印结果
    println("Formatted strings (single-threaded):")
    singleThreadResult.foreach(println)
    println("Formatted strings (multi-threaded):")
    formattedStrings.foreach(println)
  }
}
