package den.ptrq.stpete

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * @author petrique
 */

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [Application::class], webEnvironment = RANDOM_PORT)
class PingTests {

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Test
    fun `Should return pong on ping request`() {
        val response = restTemplate.getForEntity<String>("/ping")
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isEqualTo("pong")
    }
}
