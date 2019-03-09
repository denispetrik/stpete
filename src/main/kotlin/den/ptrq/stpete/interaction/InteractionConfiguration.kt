package den.ptrq.stpete.interaction

import com.fasterxml.jackson.databind.ObjectMapper
import den.ptrq.stpete.forecast.ForecastDao
import den.ptrq.stpete.forecast.ForecastMessageCreator
import den.ptrq.stpete.forecast.SunnyPeriodService
import den.ptrq.stpete.notification.NotificationSender
import den.ptrq.stpete.subscription.SubscriptionDao
import den.ptrq.stpete.telegram.TelegramClient
import org.jooq.DSLContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.support.TransactionTemplate

/**
 * @author petrique
 */
@Configuration
class InteractionConfiguration {

    @Bean
    fun interactionDao(context: DSLContext, objectMapper: ObjectMapper) = InteractionDao(context, objectMapper)

    @Bean
    fun interactionPoller(
        telegramClient: TelegramClient,
        transactionTemplate: TransactionTemplate,
        interactionDao: InteractionDao
    ) = InteractionPoller(telegramClient, transactionTemplate, interactionDao)

    @Bean
    fun interactionProcessor(
        sunnyPeriodService: SunnyPeriodService,
        forecastMessageCreator: ForecastMessageCreator,
        notificationSender: NotificationSender,
        transactionTemplate: TransactionTemplate,
        interactionDao: InteractionDao,
        subscriptionDao: SubscriptionDao,
        forecastDao: ForecastDao
    ) = InteractionProcessor(
        sunnyPeriodService,
        forecastMessageCreator,
        notificationSender,
        transactionTemplate,
        interactionDao,
        subscriptionDao,
        forecastDao
    )
}
