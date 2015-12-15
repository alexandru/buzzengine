package buzzengine.config

import buzzengine.services.Database
import buzzengine.services.rdbms.DBService
import com.typesafe.scalalogging.LazyLogging
import org.flywaydb.core.Flyway
import play.api.ApplicationLoader.Context
import play.api.{ApplicationLoader => Base, _}
import router.Routes

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class ApplicationLoader extends Base {
  override def load(context: Context): Application = {
    new MyComponents(context).application
  }
}

class MyComponents(context: Context) extends BuiltInComponentsFromContext(context)
  with LazyLogging {

  lazy val router = new Routes(
    httpErrorHandler,
    applicationController,
    assetsController)

  override lazy val configuration: Configuration = {
    val (source, cfg) = ConfigUtil.loadFromEnv()
    val initial = context.initialConfiguration

    logger.info(s"Loading configuration from $source")
    initial ++ Configuration(cfg)
  }

  implicit lazy val executionContext = actorSystem.dispatcher
  lazy val appConfig = AppConfig.load(configuration)
  lazy val database = Database(appConfig.database)

  lazy val applicationController = new buzzengine.controllers.Application()
  lazy val assetsController = new _root_.controllers.Assets(httpErrorHandler)

  locally {
    logger.info(s"Initializing database")
    Await.result(database.init(), Duration.Inf)
  }
}
