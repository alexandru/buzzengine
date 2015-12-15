package buzzengine.services

import slick.driver.JdbcDriver
import slick.jdbc.JdbcBackend
import slick.util.AsyncExecutor

/**
 * Type representing the database configuration.
 */
sealed trait DatabaseConfig

object DatabaseConfig {
  /**
   * Configuration for RDBMS databases.
   */
  case class RDBMS(
    url: String,
    user: Option[String],
    password: Option[String],
    driver: RDBMS.Driver,
    numThreads: Int,
    queueSize: Int)
    extends DatabaseConfig {

    lazy val slickDriver: JdbcDriver =
      driver.slickDriver

    lazy val database = {
      JdbcBackend.Database.forURL(url, user.orNull, password.orNull,
        driver = driver.className,
        executor = AsyncExecutor("buzz-db", numThreads, queueSize))
    }

    val migrationSet = {
      s"db/migration/${driver.id}"
    }
  }

  object RDBMS {
    /**
     * Documents supported RDBMS drivers.
     */
    sealed trait Driver {
      def id: String
      def className: String
      def slickDriver: JdbcDriver
    }

    object Driver {
      def apply(id: String): Option[Driver] =
        id match {
          case PostgreSQL.id => Some(PostgreSQL)
          case MySQL.id => Some(MySQL)
          case _ => None
        }

      case object MySQL extends Driver {
        val id = "mysql"
        val className = "com.mysql.jdbc.Driver"

        lazy val slickDriver = {
          Class.forName(className)
          slick.driver.MySQLDriver
        }
      }

      case object PostgreSQL extends Driver {
        val id = "postgresql"
        val className = "org.postgresql.Driver"

        lazy val slickDriver = {
          Class.forName(className)
          slick.driver.PostgresDriver
        }
      }

      /*
      case object H2 extends Driver {
        val id = "h2"
        val className = "org.h2.Driver"

        lazy val slickDriver = {
          Class.forName(className)
          slick.driver.H2Driver
        }
      }
       */
    }
  }
}
