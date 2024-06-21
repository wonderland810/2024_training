package configParser

import configParser.ConfigParser._

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import scala.util.matching.Regex

/**
 * @Projectname: 2024_training
 * @Filename: ConfigParser
 * @Author: Wang Le
 * @Data:2024/6/5 14:33
 * @Description: TODO
 *
 */
object ConfigParser {

  case class SwitchConfig(
                           name: String, // 开关的名称
                           enabled: Boolean = true, // 是否激活. 默认为 true
                           depList: List[Int] = List(), // 依赖的模块 id 列表. 如果为空，需要报错。
                           metaInfo: Map[String, String] = Map() // 一些元数据。默认为空 Map
                         )

  case class Result(
                     data: Option[Map[String, SwitchConfig]], // 解析后的数据
                     error: Option[String], // 第一个错误的信息
                   )
  // 如果失败，则 error 为第一个错误信息，data 为 None
  // 否则，error 为 None，data 为解析后的数据。

  def toString(result: Result): String = {
    result match {
      case Result(Some(map), None) =>
        map.map { case (key, value) =>
          val depList = value.depList.isEmpty match {
            case true => s""
            case _ => s"\n  DepList: [${value.depList.mkString(",")}]\n"
          }
          val metaInfo = value.metaInfo.isEmpty match {
            case true => s""
            case _ => s"  metaInfo:{" + value.metaInfo.map {
              case (k, v) => s"$k = $v"
            }.mkString(",") + "}"
          }
          s"$key\n  Enabled: ${value.enabled}$depList$metaInfo"
        }.mkString("\n")
      case Result(None, Some(error)) =>
        s"Error: $error"
      case _ =>
        s"Unknown error"
    }
  }

  //bonus1：逆向操作
  def stringify(map: Map[String, SwitchConfig]): String = {
    map.map { case (switchName, switchConfig) =>
      val list = switchConfig.depList.isEmpty match {
        case true => ""
        case _ => s"$switchName.depList = [${switchConfig.depList.mkString(",")}]"
      }
      val metaInfo = switchConfig.metaInfo.map {
        case (k, v) => s"$switchName.metaInfo.$k = $v"
      }.mkString("\n")
      s"$switchName.enabled = ${switchConfig.enabled}\n$list\n$metaInfo"
    }.mkString("\n")
  }
}

class ConfigParser() {

  val configMap = mutable.Map[String, SwitchConfig]() // 解析后的配置数据

  var error: Option[String] = None // 错误信息

  def parse(configStr: String): Result = {
    val lines = configStr.split("\r?\n").map(_.trim).filter(_.nonEmpty) // 以换行分割同时过滤首位空格、空字符串行
    for (elem <- lines if error.isEmpty) {
      parseLine(elem)
    }
    getResult()
  }
  //bonus3：多线程
  // 并行解析多份配置文本，并返回结果列表
  def parseAll(configStrList: List[String]): List[Result] = {
    val parsedFutures: List[Future[Result]] = configStrList.map { str =>
      Future {
        parse(str)
      }
    }
    // 等待所有Future完成并获取结果
    val parsedStrings: List[Result] = Await.result(Future.sequence(parsedFutures), 10.seconds)
    parsedStrings
  }

  //bonus2：状态维护，逐行解析
  def parseLine(configStr: String): Unit = {
    // 匹配配置项的名称和属性和值
    val switchPattern: Regex = "([a-zA-Z][a-zA-Z0-9_]*[a-zA-Z0-9])\\.([a-zA-Z0-9_.]+)\\s*=\\s*(.+)".r
    // 匹配列表
    val listPattern: Regex = "\\[(\\s*\\d+(\\s*,\\s*\\d+)*)\\s*]".r
    // 匹配metaInfo
    val metaInfoPattern: Regex = "([a-zA-Z0-9_])+\\.([a-zA-Z0-9_.]+)".r
    // 匹配boolean
    val booleanPattern: Regex = "(true|false)".r

    configStr.trim match {
      case switchPattern(switch, property, value) =>
        if (!configMap.contains(switch))
          configMap(switch) = SwitchConfig(name = switch)
        property.trim match {
          case "enabled" =>
            value.trim match {
              case booleanPattern(b) =>
                configMap(switch) = configMap(switch).copy(enabled = b.toBoolean)
              case _ =>
                error = Some(s"The $switch.enabled: Invalid boolean value")
            }
          case "depList" =>
            value.trim match {
              case listPattern(list, _) =>
                val depList = list.split(",").map(_.trim.toInt).toList

                configMap(switch) = configMap(switch).copy(depList = depList)
              case _ =>
                error = Some(s"$switch.depList: Invalid list format")
            }
          case metaInfoPattern(_, key) =>
            val map = configMap(switch).metaInfo + (key -> value.trim)
            configMap(switch) = configMap(switch).copy(metaInfo = map)
          case _ =>
            error = Some(s"The SwitchConfig string '$configStr' exist an invalid property: '$property'.")
        }
      case _ =>
        error = Some(s"The SwitchConfig string '$configStr' does not match the expected format.")
    }
  }

  // 获得解析结果
  def getResult(): Result = {
    error match {
      case Some(err) => Result(None, Some(err))
      case None => Result(Some(configMap.toMap), None)
    }
  }
}

