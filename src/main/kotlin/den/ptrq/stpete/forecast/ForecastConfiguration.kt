package den.ptrq.stpete.forecast

import den.ptrq.stpete.notification.NotificationSender
import den.ptrq.stpete.subscription.SubscriptionDao
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Value
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
    fun forecastClient(
        restTemplate: RestTemplate,
        @Value("\${forecastToken}") token: String
    ) = ForecastClient(restTemplate, token)

    @Bean
    fun diffCalculator() = DiffCalculator()

    @Bean
    fun forecastChecker(
        forecastClient: ForecastClient,
        notificationSender: NotificationSender,
        diffCalculator: DiffCalculator,
        transactionTemplate: TransactionTemplate,
        forecastDao: ForecastDao,
        subscriptionDao: SubscriptionDao
    ) = ForecastChecker(
        forecastClient,
        notificationSender,
        diffCalculator,
        transactionTemplate,
        forecastDao,
        subscriptionDao
    )
}
