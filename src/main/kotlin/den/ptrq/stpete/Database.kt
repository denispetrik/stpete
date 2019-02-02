package den.ptrq.stpete

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

/**
 * @author petrique
 */

@Configuration
class DatabaseConfiguration {

    @Bean
    fun dataSource(): DataSource {
        val hikariConfig = HikariConfig()
        hikariConfig.driverClassName = "org.hsqldb.jdbc.JDBCDriver"
        hikariConfig.jdbcUrl = "jdbc:hsqldb:mem:stpete"
        hikariConfig.username = "sa"
        hikariConfig.password = ""
        hikariConfig.isAutoCommit = false
        hikariConfig.addDataSourceProperty("sql.syntax_pgs", "true")
        return HikariDataSource(hikariConfig)
    }

    @Bean
    fun flywayMigrationStrategy() = FlywayMigrationStrategy { flyway ->
        flyway.setSchemas("p")
        flyway.migrate()
    }
}
