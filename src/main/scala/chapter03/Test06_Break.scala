package chapter03

import scala.util.control.Breaks
import scala.util.control.Breaks._

object Test06_Break {
  def main(args: Array[String]): Unit = {
    // 1. 采用抛出异常的方式，退出循环
    try {
      for (i <- 0 until 5) {
        if (i == 3)
          throw new RuntimeException
        println(i)
      }
    } catch {
      case e: Exception => // 什么都不做，只是退出循环
    }

    // 2. 使用Scala中的Breaks类的break方法，实现异常的抛出和捕捉
    Breaks.breakable(
      for (i <- 0 until 5) {
        if (i == 3)
          Breaks.break()
        println(i)
      }
    )

    breakable(
      for (i <- 0 until 5) {
        if (i == 3)
          break()
        println(i)
      }
    )

    println("这是循环外的代码")

    val arr = Array(1, 2, 3)

    val arr2 = for (elem <- arr) yield {
      elem * 2
    }

    for (elem <- arr2.tail.tail) {
      println(elem)
    }

    val tp = ("wangle",23,171)
    println(tp._1)
    tp.formatted("wangl")
    println(tp._1)
    println(tp._2)
    println(tp._3)
  }
}
