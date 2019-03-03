package den.ptrq.stpete

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.net.URI
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.sql.DataSource

/**
 * @author petrique
 */

@Configuration
class DatabaseConfiguration {

    fun dataSource(): DataSource {
        val hikariConfig = HikariConfig()
        hikariConfig.driverClassName = "org.h2.Driver"
        hikariConfig.jdbcUrl = "jdbc:h2:mem:db;MODE=PostgreSQL;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=10"
        hikariConfig.username = "sa"
        hikariConfig.password = ""
        hikariConfig.isAutoCommit = false
        return HikariDataSource(hikariConfig)
    }

    @Bean
    fun dataSource(@Value("\${database.url}") databaseUrl: URI): DataSource {
        val hikariConfig = HikariConfig()
        hikariConfig.driverClassName = "org.postgresql.Driver"
        hikariConfig.jdbcUrl = "jdbc:postgresql://${databaseUrl.host}:${databaseUrl.port}${databaseUrl.path}"
        hikariConfig.username = databaseUrl.userInfo.split(":")[0]
        hikariConfig.password = databaseUrl.userInfo.split(":")[1]
        hikariConfig.isAutoCommit = false
        hikariConfig.addDataSourceProperty("sslmode", "require")
        return HikariDataSource(hikariConfig)
    }

    @Bean
    fun flywayMigrationStrategy() = FlywayMigrationStrategy { flyway ->
        flyway.setSchemas("public")
        flyway.migrate()
    }
}

fun Instant.asTimestamp() = Timestamp.from(this)

fun LocalDateTime.asTimestamp() = this.toInstant(ZoneOffset.UTC).asTimestamp()

fun Timestamp.asLocalDateTime() = this.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime()
