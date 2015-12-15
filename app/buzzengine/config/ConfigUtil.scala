package buzzengine.config

import java.io.File
import com.typesafe.config.{Config, ConfigFactory}
import play.api.libs.json._

object ConfigUtil {
  /**
   * For tracking the source of the servers configuration.
   */
  sealed trait ConfigSource

  object ConfigSource {
    case object FromString extends ConfigSource
    case object FromInputStream extends ConfigSource
    case object FromDatabase extends ConfigSource
    case class Resource(name: String) extends ConfigSource
    case class FilePath(path: String) extends ConfigSource

    implicit val jsonFormat = new Format[ConfigSource] {
      val IsResource = """^Resource[(]([^)]+)[)]$""".r
      val IsFilePath = """^FilePath[(]([^)]+)[)]$""".r

      def reads(json: JsValue) =
        json.validate[String].flatMap {
          case "FromString" => JsSuccess(FromString)
          case "FromInputStream" => JsSuccess(FromInputStream)
          case "FromDatabase" => JsSuccess(FromDatabase)
          case IsResource(name) => JsSuccess(Resource(name))
          case IsFilePath(path) => JsSuccess(FilePath(path))
          case other => JsError("Invalid config source ID: " + other)
        }
      def writes(o: ConfigSource) = {
        JsString(o.toString)
      }
    }
  }

  def getConfigSource: ConfigSource =
    Option(System.getProperty("config.file")) match {
      case Some(path) if new File(path).exists() =>
        ConfigSource.FilePath(path)

      case _ =>
        val opt1 = Option(System.getProperty("ENV", "")).filter(_.nonEmpty)
        val opt2 = Option(System.getProperty("env", "")).filter(_.nonEmpty)

        opt1.orElse(opt2) match {
          case Some(envName) =>
            val name = s"application.${envName.toLowerCase}.conf"
            ConfigSource.Resource(name)
          case None =>
            ConfigSource.Resource("application.dev.conf")
        }
    }

  def loadFromEnv(): (ConfigSource, Config) = {
    getConfigSource match {
      case ref @ ConfigSource.FilePath(path) =>
        (ref, ConfigFactory.parseFile(new File(path)).resolve())

      case ref @ ConfigSource.Resource(name) =>
        (ref, ConfigFactory.load(name).resolve())

      case _ =>
        (ConfigSource.FromInputStream, ConfigFactory.load().resolve())
    }
  }
}
