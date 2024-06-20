package partialFunction


/**
 * @Projectname: 2024_training
 * @Filename: Test01
 * @Author: Wang Le
 * @Data:2024/6/7 12:25
 * @Description: TODO
 *
 */
object Test01 {

  def main(args: Array[String]): Unit = {
    /**
     * 一个输入参数为Int类型，返回值为String类型的偏函数
     */
    val function0: PartialFunction[Int, String] = {
      case 1 => "一"
      case 2 => "二"
      case 3 => "三"
      case _ => "其它"
    }
    println(function0(1))

    //定义一个列表，包含0-10的数字
    //将1-3的数字都转换为[1-3]
    //请将4-8的数字都转换为[4-8]
    //将大于8的数字转换为(8-*]
    //将其它的数字转化成其它
    val list = (0 to 10).toList
    val list2 = list.map {
      case x if x >= 1 && x <= 3 => "[1-3]"
      case x if x >= 4 && x <= 8 => "[4-8]"
      case x if x > 8 => "(8-*]"
      case _ => "其它"
    }
    println(list2)
  }
}

