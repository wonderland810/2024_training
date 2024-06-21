package option_learning

/**
 * @Projectname: 2024_training
 * @Filename: Option01
 * @Author: Wang Le
 * @Data:2024/6/5 19:18
 * @Description: TODO
 *
 */
object Option01 {
  def main(args: Array[String]): Unit = {
    // 虽然 Scala 可以不定义变量的类型，不过为了清楚些，我还是
    // 把他显示的定义上了

    val myMap: Map[String, String] = Map("key1" -> "value1")
    val value1: Option[String] = myMap.get("key1")
    val value2: Option[String] = myMap.get("key2")

    println(value1) // Some("value1")
    println(value2) // None

    println("=======================")
    val sites = Map("runoob" -> "www.runoob.com", "google" -> "www.google.com")

    println("sites.get( \"runoob\" ) : " + sites.get("runoob")) // Some(www.runoob.com)
    println("sites.get( \"baidu\" ) : " + sites.get("baidu")) //  None
    println("=======================")
    val a:Option[Int] = Some(5)
    val b:Option[Int] = None

    println("a.getOrElse(0): " + a.getOrElse(0) )
    println("b.getOrElse(10): " + b.getOrElse(10) )
    println("=======================")
    val s = "1, 2,3 ,4,5"
    val depList = s.split(",").toList
    println(depList)


    val map = Map("key1" -> "value1", "key2" -> "value2", "key3" -> "value3")

    // 自定义格式化函数
    def formatMap(map: Map[String, String]): String = {
      map.map { case (key, value) => s"[$key=$value]" }.mkString(", ")
    }

    // 使用自定义格式化函数
    val formattedString = formatMap(map)
    println(formattedString)  // 输出: [key1=value1], [key2=value2], [key3=value3]
  }

}
