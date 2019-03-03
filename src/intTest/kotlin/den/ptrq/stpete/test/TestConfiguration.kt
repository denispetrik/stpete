package den.ptrq.stpete.test

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import javax.sql.DataSource

/**
 * @author petrique
 */
@Configuration
class TestConfiguration {

    @Primary
    @Bean
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
    fun testUtils() = TestUtils()
}
