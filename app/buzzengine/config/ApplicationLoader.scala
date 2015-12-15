package buzzengine.config

import buzzengine.db.DBService
import com.typesafe.scalalogging.LazyLogging
import org.flywaydb.core.Flyway
import play.api.ApplicationLoader.Context
import play.api.{ApplicationLoader => Base, _}
import router.Routes

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
  lazy val dbService = new DBService(appConfig.database)

  lazy val applicationController = new buzzengine.controllers.Application()
  lazy val assetsController = new _root_.controllers.Assets(httpErrorHandler)

  locally {
    logger.info(s"Initializing database")
    val db = appConfig.database
    val flyway = new Flyway
    flyway.setDataSource(db.url, db.user.orNull, db.password.orNull)
    flyway.migrate()
  }
}
