package den.ptrq.stpete.interaction

import com.fasterxml.jackson.databind.ObjectMapper
import den.ptrq.stpete.subscription.SubscriptionDao
import den.ptrq.stpete.telegram.TelegramClient
import org.jooq.DSLContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.support.TransactionTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

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
        transactionTemplate: TransactionTemplate,
        interactionDao: InteractionDao,
        subscriptionDao: SubscriptionDao
    ) = InteractionProcessor(transactionTemplate, interactionDao, subscriptionDao)

    @Bean
    fun interactionController(interactionDao: InteractionDao) = InteractionController(interactionDao)
}

@RestController
class InteractionController(private val interactionDao: InteractionDao) {
    @GetMapping("/interactions")
    fun getAll(): String {
        return interactionDao.selectAll().asSequence()
            .map { it.toString() }
            .joinToString(separator = "\n")
    }
}
