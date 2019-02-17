package den.ptrq.stpete.subscription

import den.ptrq.stpete.forecast.Forecast
import den.ptrq.stpete.telegram.TelegramClient
import org.slf4j.LoggerFactory
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * @author petrique
 */
class NotificationSender(private val telegramClient: TelegramClient) {

    fun sendNotification(subscription: Subscription, forecastList: List<Forecast>) {
        log.info("sendNotification({}, {})", subscription, forecastList)

        val message = forecastList.asSequence()
            .map { ZonedDateTime.ofInstant(Instant.ofEpochSecond(it.epochTime), ZoneId.of("+3")) }
            .groupBy { it.dayOfMonth }
            .map { (day, dates) ->
                val hours = dates.joinToString(separator = "; ") { it.hour.toString() }
                "day = $day, hours: $hours"
            }
            .joinToString(separator = "\n")

        telegramClient.sendMessage(chatId = subscription.chatId, text = message)
    }

    companion object {
        private val log = LoggerFactory.getLogger(NotificationSender::class.java)
    }
}
