package den.ptrq.stpete

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @author petrique
 */

@Configuration
class PingConfiguration {
    @Bean
    fun pingController() = PingController()
}

@RestController
class PingController {
    @GetMapping("/ping")
    fun ping() = "pong"
}
