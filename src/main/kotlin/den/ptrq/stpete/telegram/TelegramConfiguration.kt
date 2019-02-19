package den.ptrq.stpete.telegram

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

/**
 * @author petrique
 */
@Configuration
class TelegramConfiguration {

    @Bean
    fun telegramClient(
        restTemplate: RestTemplate,
        @Value("\${telegramToken}") token: String
    ) = TelegramClient(restTemplate, token)
}
