package den.ptrq.stpete.forecast

import den.ptrq.stpete.client.telegram.TelegramClient
import den.ptrq.stpete.subscription.SubscriptionDao
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

/**
 * @author petrique
 */
@Configuration
class ForecastConfiguration {

    @Bean
    fun forecastClient(restTemplate: RestTemplate) = ForecastClient(restTemplate)

    @Bean
    fun forecastChecker(
        forecastClient: ForecastClient,
        telegramClient: TelegramClient,
        subscriptionDao: SubscriptionDao
    ) = ForecastChecker(forecastClient, telegramClient, subscriptionDao)
}
