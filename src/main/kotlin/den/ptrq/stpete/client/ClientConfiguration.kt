package den.ptrq.stpete.client

import den.ptrq.stpete.client.telegram.TelegramClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

/**
 * @author petrique
 */
@Configuration
class ClientConfiguration {
    @Bean
    fun telegramClient(restTemplate: RestTemplate) = TelegramClient(restTemplate)
}
