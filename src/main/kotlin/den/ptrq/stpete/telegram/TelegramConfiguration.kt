package den.ptrq.stpete.telegram

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

/**
 * @author petrique
 */
@Configuration
class TelegramConfiguration {

    @Bean
    fun telegramClient(restTemplate: RestTemplate) = TelegramClient(restTemplate)
}
