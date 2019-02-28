package den.ptrq.stpete.test

import den.ptrq.stpete.Application
import org.springframework.boot.test.context.SpringBootTest

/**
 * @author petrique
 */
@SpringBootTest(
    classes = [Application::class, TestConfiguration::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
abstract class IntTests
