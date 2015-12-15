package buzzengine.db

import com.typesafe.scalalogging.StrictLogging
import org.flywaydb.core.Flyway
import scala.concurrent.{Future, ExecutionContext}

final class DBService(val dbConfig: DBConfig)(implicit ec: ExecutionContext)
  extends StrictLogging {

  val schema = new DBSchema(dbConfig.slickDriver)
  val database = dbConfig.database

  import schema._
  import schema.driver.api._

  /* Initialized once per DBService instance after pula is done */
  private[this] lazy val _init = Future {
    logger.info(s"Migrating database")
    val flyway = new Flyway
    flyway.setDataSource(dbConfig.url, dbConfig.user.orNull, dbConfig.password.orNull)
    flyway.migrate()
  }

  /**
   * Executes the database migrations, is idempotent.
   */
  def init() = _init

  def fetchActiveArticles(offset: Int = 0, count: Int = 10): Future[Seq[ArticleRow]] = {
    val q = ArticlesTable.active.sortBy(_.createdAt).drop(offset).take(count)
    database.run(q.result)
  }
}
