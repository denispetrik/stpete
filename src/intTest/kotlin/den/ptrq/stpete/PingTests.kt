package den.ptrq.stpete

import den.ptrq.stpete.test.IntTests
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.http.HttpStatus

/**
 * @author petrique
 */
class PingTests : IntTests() {

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Test
    fun `should return pong on ping request`() {
        val response = restTemplate.getForEntity<String>("/ping")
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isEqualTo("pong")
    }
}
