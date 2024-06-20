package implicit_learning

/**
 * @Projectname: 2024_training
 * @Filename: implict_learnning.implicit_learnning
 * @Author: Wang Le
 * @Data:2024/6/6 19:18
 * @Description: TODO
 *
 */
object Implicit {
  def main(args: Array[String]): Unit = {
//    def circle(radius: Int, pi: Double) = {
//      radius * 2 * pi
//    }

    def circle1(radius: Int)(pi: Double = 3.14) = {
      radius * 2 * pi
    }

//    println(circle(2, 3.14))
    println(circle1(2)(3.14))

    println("================================")

    implicit val num:Int = 10     //定义一个Int类型的隐式参数num
    implicit val say:String="Hello scala"   //定义一个String类型的隐式参数say
    def sum(a:Int)(implicit b :Int = 20,str:String = "Hello"):Int={   //请注意这里传参时，将 num 改成了 b，say 改成了str ，证明隐式参数只认类型
      println(str)
      a+b
    }
    //测试
    println(sum(10))

    //控制台输出：
    //Hello scala
    //20

  }
}
