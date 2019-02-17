package den.ptrq.stpete.forecast

import den.ptrq.stpete.subscription.NotificationSender
import den.ptrq.stpete.subscription.SubscriptionDao
import org.jooq.DSLContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.support.TransactionTemplate
import org.springframework.web.client.RestTemplate

/**
 * @author petrique
 */
@Configuration
class ForecastConfiguration {

    @Bean
    fun forecastDao(context: DSLContext) = ForecastDao(context)

    @Bean
    fun forecastClient(restTemplate: RestTemplate) = ForecastClient(restTemplate)

    @Bean
    fun forecastChecker(
        forecastClient: ForecastClient,
        notificationSender: NotificationSender,
        transactionTemplate: TransactionTemplate,
        forecastDao: ForecastDao,
        subscriptionDao: SubscriptionDao
    ) = ForecastChecker(forecastClient, notificationSender, transactionTemplate, forecastDao, subscriptionDao)
}
