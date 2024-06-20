package chapter02

/**
 * @Projectname: 2024_training
 * @Filename: String
 * @Author: Wang Le
 * @Data:2024/5/24 21:46
 * @Description: TODO
 *
 */
object String {
  def main(args: Array[String]): Unit = {
    //（1）字符串，通过+号连接
    val name: String = "wanghaha"
    val age: Int = 28
    println(age + "岁的" + name + "hai mei you nv py")
    println("===================")

    // *用于将一个字符串复制多次并拼接
    println(name * 3)
    println("===================")

    //（2）printf用法：字符串，通过%传值。
    printf("%d岁的%shai mei you nv py", age, name)
    println('\n' + "===================")

    //（3）字符串模板（插值字符串）：通过$获取变量值
    println(s"${age}岁的${name}hai mei you nv py")

    val num: Double = 2.3456
    println(f"The num is ${num}%2.2f") // 格式化模板字符串
    println(raw"The num is ${num}%2.2f")

    // 三引号表示字符串，保持多行字符串的原格式输出
    val sql =
      s"""
         |select *
         |from
         |  student
         |where
         |  name = ${name}
         |and
         |  age > ${age}
         |""".stripMargin
    println(sql)
  }

}
