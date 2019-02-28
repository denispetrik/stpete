package den.ptrq.stpete.forecast

import den.ptrq.stpete.notification.NotificationSender
import den.ptrq.stpete.subscription.SubscriptionDao
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.transaction.support.TransactionTemplate
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * @author petrique
 */
class ForecastChecker(
    private val forecastClient: ForecastClient,
    private val notificationSender: NotificationSender,
    private val diffCalculator: DiffCalculator,
    private val transactionTemplate: TransactionTemplate,
    private val forecastDao: ForecastDao,
    private val subscriptionDao: SubscriptionDao
) {

    @Scheduled(fixedRate = 10000, initialDelay = 10000)
    fun checkForecast() {
        log.info("checking forecast")

        val forecastResponse = forecastClient.getForecast()
        val newForecastItems = forecastResponse.forecastItems
        val oldForecasts = forecastDao.selectActual()

        transactionTemplate.execute {
            val newForecasts = upsertAll(newForecastItems, oldForecasts)

            val diff = diffCalculator.calculateDiff(newForecasts, oldForecasts)
            val message = formMessage(diff)

            subscriptionDao.selectAll().forEach {
                notificationSender.sendAsynchronously(it.chatId, message)
            }
        }
    }

    private fun upsertAll(
        newForecastItems: List<ForecastItem>,
        oldForecasts: List<Forecast>
    ): List<Forecast> {
        val oldForecastMap = oldForecasts.associateBy { it.epochTime }

        return newForecastItems.map { newOne ->
            val oldOne = oldForecastMap[newOne.date]
            if (oldOne == null) {
                Forecast(forecastDao.generateForecastId(), newOne.date, newOne.clouds.percentage)
                    .apply { forecastDao.insert(this) }
            } else {
                Forecast(oldOne.id, oldOne.epochTime, newOne.clouds.percentage)
                    .apply { forecastDao.updateClouds(this) }
            }
        }
    }

    private fun formMessage(forecastList: List<Forecast>): String {
        return forecastList.asSequence()
            .map { ZonedDateTime.ofInstant(Instant.ofEpochSecond(it.epochTime), ZoneId.of("+3")) }
            .groupBy { it.dayOfMonth }
            .map { (day, dates) ->
                val hours = dates.joinToString(separator = "; ") { it.hour.toString() }
                "day = $day, hours: $hours"
            }
            .joinToString(separator = "\n")
    }

    companion object {
        private val log = LoggerFactory.getLogger(ForecastChecker::class.java)
    }
}
