package den.ptrq.stpete.forecast

import den.ptrq.stpete.notification.NotificationSender
import den.ptrq.stpete.subscription.SubscriptionDao
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.support.TransactionTemplate
import org.springframework.web.client.RestTemplate
import java.time.LocalTime

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
    fun sunnyPeriodService(
        @Value("#{T(java.time.LocalTime).parse('\${daytime.startOfDay}')}") startOfDay: LocalTime,
        @Value("#{T(java.time.LocalTime).parse('\${daytime.endOfDay}')}") endOfDay: LocalTime
    ) = SunnyPeriodService(startOfDay, endOfDay)

    @Bean
    fun forecastMessageCreator() = ForecastMessageCreator()

    @Bean
    fun forecastChecker(
        forecastClient: ForecastClient,
        sunnyPeriodService: SunnyPeriodService,
        notificationSender: NotificationSender,
        forecastMessageCreator: ForecastMessageCreator,
        transactionTemplate: TransactionTemplate,
        forecastDao: ForecastDao,
        subscriptionDao: SubscriptionDao
    ) = ForecastChecker(
        forecastClient,
        sunnyPeriodService,
        forecastMessageCreator,
        notificationSender,
        transactionTemplate,
        forecastDao,
        subscriptionDao
    )
}
