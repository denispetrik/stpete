package den.ptrq.stpete.interaction

import den.ptrq.stpete.TEN_SECONDS
import den.ptrq.stpete.THIRTY_SECONDS
import den.ptrq.stpete.forecast.ForecastDao
import den.ptrq.stpete.forecast.ForecastMessageCreator
import den.ptrq.stpete.forecast.SunnyPeriodService
import den.ptrq.stpete.notification.NotificationSender
import den.ptrq.stpete.subscription.Subscription
import den.ptrq.stpete.subscription.SubscriptionDao
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.transaction.support.TransactionTemplate

/**
 * @author petrique
 */
class InteractionProcessor(
    private val forecastMessageCreator: ForecastMessageCreator,
    private val sunnyPeriodService: SunnyPeriodService,
    private val notificationSender: NotificationSender,
    private val transactionTemplate: TransactionTemplate,
    private val interactionDao: InteractionDao,
    private val subscriptionDao: SubscriptionDao,
    private val forecastDao: ForecastDao
) {

    @Scheduled(fixedRate = TEN_SECONDS, initialDelay = THIRTY_SECONDS)
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
                val subscription = createSubscription(interaction)
                subscriptionDao.insert(subscription)
                sendSunnyDaysMessage(subscription)
            }
        }
    }

    private fun sendSunnyDaysMessage(subscription: Subscription) {
        val actualForecasts = forecastDao.selectActual()
        val sunnyForecasts = sunnyPeriodService.filterSunny(actualForecasts)
        if (sunnyForecasts.isNotEmpty()) {
            val message = forecastMessageCreator.createSunnyDaysMessage(sunnyForecasts)
            notificationSender.sendAsynchronously(subscription.chatId, message)
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
