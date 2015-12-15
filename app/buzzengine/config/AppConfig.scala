package buzzengine.config

import buzzengine.db.DBConfig
import com.typesafe.config.Config
import play.api
import scala.util.Try

case class AppConfig(
  database: DBConfig)

object AppConfig {
  def load(): AppConfig = {
    val (_, config) = ConfigUtil.loadFromEnv()
    load(config)
  }

  def load(config: api.Configuration): AppConfig =
    load(config.underlying)

  def load(config: Config): AppConfig =
    AppConfig(
      database = DBConfig(
        url = config.getString("db.default.url"),
        user = Try(config.getString("db.default.user")).toOption.filterNot(_.isEmpty),
        password = Try(config.getString("db.default.password")).toOption.filterNot(_.isEmpty),
        driver = config.getString("db.default.driver"),
        numThreads = config.getInt("db.default.numThreads"),
        queueSize = config.getInt("db.default.queueSize")
      ))
}
