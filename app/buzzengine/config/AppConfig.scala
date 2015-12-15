package buzzengine.config

import buzzengine.services.DatabaseConfig
import com.typesafe.config.Config
import com.typesafe.config.ConfigException.BadValue
import play.api
import scala.util.Try

case class AppConfig(
  database: DatabaseConfig
)

object AppConfig {
  def load(): AppConfig = {
    val (_, config) = ConfigUtil.loadFromEnv()
    load(config)
  }

  def load(config: api.Configuration): AppConfig =
    load(config.underlying)

  def load(config: Config): AppConfig = {
    def rdbms = DatabaseConfig.RDBMS(
      url = config.getString("database.rdbms.url"),
      user = Try(config.getString("database.rdbms.user")).toOption.filterNot(_.isEmpty),
      password = Try(config.getString("database.rdbms.password")).toOption.filterNot(_.isEmpty),
      driver = {
        val id = config.getString("database.rdbms.driver")
        DatabaseConfig.RDBMS.Driver(id).getOrElse(
          throw new BadValue("database.rdbms.driver", s"ID is not recognized: $id"))
      },
      numThreads = config.getInt("database.rdbms.numThreads"),
      queueSize = config.getInt("database.rdbms.queueSize")
    )

    AppConfig(database =
      config.getString("database.type") match {
        case "rdbms" => rdbms
      })
  }
}
