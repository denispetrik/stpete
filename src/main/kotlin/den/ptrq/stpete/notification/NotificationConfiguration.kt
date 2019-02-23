package den.ptrq.stpete.notification

import den.ptrq.stpete.telegram.TelegramClient
import org.jooq.DSLContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.support.TransactionTemplate

/**
 * @author petrique
 */
@Configuration
class NotificationConfiguration {

    @Bean
    fun notificationDao(context: DSLContext) = NotificationDao(context)

    @Bean
    fun notificationSender(
        telegramClient: TelegramClient,
        transactionTemplate: TransactionTemplate,
        notificationDao: NotificationDao
    ) = NotificationSender(telegramClient, transactionTemplate, notificationDao)
}
