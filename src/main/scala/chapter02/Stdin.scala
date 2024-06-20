package chapter02

import scala.io.StdIn


object Stdin {
  def main(args: Array[String]): Unit = {
    // 输入信息
    println("input your name：")
    val name: String = StdIn.readLine()
    println("please input your age：")
    val age: Int = StdIn.readInt()

    // 控制台打印输出
    println(s"age：${age}\n姓名：${name}")
  }
}
