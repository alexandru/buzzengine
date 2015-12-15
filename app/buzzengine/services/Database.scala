package buzzengine.services

import buzzengine.services.rdbms.DBService

import scala.concurrent.{ExecutionContext, Future}

trait Database {
  /**
   * Initializes the database (runs migrations, whatever).
   */
  def init(): Future[Unit]
}

object Database {
  def apply(config: DatabaseConfig)(implicit ec: ExecutionContext): Database =
    config match {
      case c @ DatabaseConfig.RDBMS(_,_,_,_,_,_) =>
        new DBService(c)
    }
}
