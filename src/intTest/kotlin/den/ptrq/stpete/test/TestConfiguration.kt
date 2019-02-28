package den.ptrq.stpete.test

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * @author petrique
 */
@Configuration
class TestConfiguration {
    @Bean
    fun testUtils() = TestUtils()
}
