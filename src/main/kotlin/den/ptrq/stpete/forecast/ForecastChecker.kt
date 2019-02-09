package den.ptrq.stpete.forecast

import den.ptrq.stpete.client.telegram.TelegramClient
import den.ptrq.stpete.subscription.Subscription
import den.ptrq.stpete.subscription.SubscriptionDao
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * @author petrique
 */
class ForecastChecker(
    private val forecastClient: ForecastClient,
    private val telegramClient: TelegramClient,
    private val subscriptionDao: SubscriptionDao
) {

    @Scheduled(fixedRate = 10000, initialDelay = 10000)
    fun checkForecast() {
        log.info("checking forecast")

        val forecastResponse = forecastClient.getForecast()

        val sunnyForecastItem = forecastResponse.forecastItems
            .sortedBy { it.date }
            .firstOrNull { it.clouds.percentage < 50 }

        if (sunnyForecastItem != null) {
            log.info("there will be a sunny day")
            subscriptionDao.selectAll().asSequence()
                .forEach { sendNotification(it, sunnyForecastItem) }
        }
    }

    private fun sendNotification(subscription: Subscription, forecastItem: ForecastItem) {
        val zonedDateTime = ZonedDateTime.ofInstant(
            Instant.ofEpochSecond(forecastItem.date),
            ZoneId.of("+3")
        )
        telegramClient.sendMessage(
            chatId = subscription.chatId,
            text = "sunny time: $zonedDateTime"
        )
    }

    companion object {
        private val log = LoggerFactory.getLogger(ForecastChecker::class.java)
    }
}
