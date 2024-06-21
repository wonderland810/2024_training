package configParser

import configParser.ConfigParser.Result

import scala.collection.mutable.ListBuffer

/**
 * @Projectname: 2024_training
 * @Filename: Main
 * @Author: Wang Le
 * @Data:2024/6/5 14:27
 * @Description: TODO
 *
 */
object Main {
  def main(args: Array[String]): Unit = {
    val configStr: String =
      s"""
    switchA.enabled = true
    switchA.depList = [1,  2, 3  ,   5,4  ]
    switchA.metaInfo.owner = "userA"
    switchA.metaInfo.comment = "hello world"


    switchB.enabled = false
    switchB.depList = [1]
    switchB.metaInfo.owner = "userB"

    switchB.metaInfo.comment = "hello world"
    """
    //测试parse方法
    val parser = new ConfigParser()
    val result1: ConfigParser.Result = parser.parse(configStr)
    println("=====================测试parse方法======================")
    println(ConfigParser.toString(result1))

    println("=====================bonus1测试逆向操作======================")
    //bonus1：逆向操作
    println(ConfigParser.stringify(result1.data.get))

    println("===================bonus2状态维护，逐行解析======================")
    //bonus2：状态维护，逐行解析
    val parser2 = new ConfigParser()
    parser2.parseLine("switchA.enabled = true")
    parser2.parseLine("switchA.depList = [1, 2, 3]")
    parser2.parseLine("switchA.metaInfo.owner = \"userA\"")
    parser2.parseLine("switchA.metaInfo.comment = \"hello world\"")

    parser2.parseLine("switchB.enabled = false")

    val result2: Result = parser2.getResult()
    println(ConfigParser.toString(result2))


    //bonus3：多线程
    println("===================bonus3多线程======================")
    val lb: ListBuffer[String] = ListBuffer()
    val singleThreadStart = System.nanoTime()
    for (i <- 1 to 500) {
      lb.append(configStr)
      parser.parse(configStr)
    }
    println("parse(single-threaded)耗时:" + (System.nanoTime() - singleThreadStart) / 1e6 + "ms")
    val multiThreadStart = System.nanoTime()
    parser.parseAll(lb.toList)
//    val results = parser.parseAll(lb.toList)
//    results.foreach(re =>print(ConfigParser.toString(re)))
    println("parseAll(multi-threaded)耗时:" + (System.nanoTime() - multiThreadStart) / 1e6 + "ms")
  }
}
