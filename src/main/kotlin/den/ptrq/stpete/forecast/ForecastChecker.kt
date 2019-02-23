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
    private val transactionTemplate: TransactionTemplate,
    private val forecastDao: ForecastDao,
    private val subscriptionDao: SubscriptionDao
) {

    @Scheduled(fixedRate = 10000, initialDelay = 10000)
    fun checkForecast() {
        log.info("checking forecast")

        val forecastResponse = forecastClient.getForecast()

        val diff = formDiff(forecastResponse.forecastItems)

        if (diff.isNotEmpty()) {
            log.info("there will be some sun")

            transactionTemplate.execute {
                val forecastList = diff.map {
                    if (it.isNew()) {
                        Forecast(forecastDao.generateForecastId(), it.epochTime, it.clouds)
                            .apply { forecastDao.insert(this) }
                    } else {
                        Forecast(it.id!!, it.epochTime, it.clouds)
                            .apply { forecastDao.updateClouds(this) }
                    }
                }

                val message = formMessage(forecastList)

                subscriptionDao.selectAll().forEach {
                    notificationSender.sendAsynchronously(it.chatId, message)
                }
            }
        }
    }

    private fun formDiff(newForecastItems: List<ForecastItem>): List<Diff> {
        val oldForecastMap = forecastDao.getActual().associateBy { it.epochTime }

        val diff = mutableListOf<Diff>()

        newForecastItems.forEach { newForecastItem ->
            val oldForecast = oldForecastMap[newForecastItem.date]
            if (oldForecast == null) {
                if (newForecastItem.isSunny()) {
                    diff += Diff.with(newForecastItem)
                }
            } else {
                if (newForecastItem.differsFrom(oldForecast)) {
                    diff += Diff.with(oldForecast.id, newForecastItem)
                }
            }
        }

        return diff
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

    private fun ForecastItem.differsFrom(forecast: Forecast) = this.isSunny() != forecast.isSunny()
    private fun Forecast.isSunny() = clouds <= 40
    private fun ForecastItem.isSunny() = clouds.percentage <= 40

    companion object {
        private val log = LoggerFactory.getLogger(ForecastChecker::class.java)
    }
}

class Diff(
    val id: Long?,
    val epochTime: Long,
    val clouds: Int
) {
    fun isNew() = id == null

    companion object {
        fun with(forecastItem: ForecastItem) = Diff(
            id = null,
            epochTime = forecastItem.date,
            clouds = forecastItem.clouds.percentage
        )

        fun with(id: Long, forecastItem: ForecastItem) = Diff(
            id = id,
            epochTime = forecastItem.date,
            clouds = forecastItem.clouds.percentage
        )
    }
}
