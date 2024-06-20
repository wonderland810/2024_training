package chapter01

/**
 * @Projectname: 2024_training
 * @Filename: Student
 * @Author: Wang Le
 * @Data:2024/5/23 21:40
 * @Description: TODO
 *
 */

class Student(name: String, var age: Int) {
  def printInfo(): Unit = {
    println(name + " " + age + " " + Student.school)
  }
}

object Student {
  val school: String = "cug"

  def main(args: Array[String]): Unit = {
    val alice = new Student("alice", 20)
    val bob = new Student("bob", 23)

    alice.printInfo()
    bob.printInfo()
  }
}
