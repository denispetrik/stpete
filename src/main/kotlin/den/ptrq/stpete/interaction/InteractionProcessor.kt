package den.ptrq.stpete.interaction

import den.ptrq.stpete.subscription.Subscription
import den.ptrq.stpete.subscription.SubscriptionDao
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.transaction.support.TransactionTemplate

/**
 * @author petrique
 */
class InteractionProcessor(
    private val transactionTemplate: TransactionTemplate,
    private val interactionDao: InteractionDao,
    private val subscriptionDao: SubscriptionDao
) {

    @Scheduled(fixedRate = 10000, initialDelay = 10000)
    fun processInteractions() {
        log.info("processing interactions")
        interactionDao.selectUnprocessed(limit = 3).forEach { process(it) }
    }

    private fun process(interaction: Interaction) {
        log.info("process({})", interaction)

        val isStartCommandPresent = interaction.keyWords.asSequence()
            .filter { it.type == KeyWord.Type.BOT_COMMAND }
            .any { it.value == "start" }

        transactionTemplate.execute {
            interactionDao.markAsProcessed(interaction)
            if (isStartCommandPresent) {
                subscriptionDao.insert(createSubscription(interaction))
            }
        }
    }

    private fun createSubscription(interaction: Interaction) = Subscription(
        id = subscriptionDao.generateSubscriptionId(),
        userId = interaction.userId,
        userName = interaction.userName,
        chatId = interaction.chatId,
        chatType = interaction.chatType
    )

    companion object {
        private val log = LoggerFactory.getLogger(InteractionProcessor::class.java)
    }
}
