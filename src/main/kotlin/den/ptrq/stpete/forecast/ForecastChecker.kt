package den.ptrq.stpete.forecast

import den.ptrq.stpete.subscription.SubscriptionDao
import den.ptrq.stpete.telegram.TelegramClient
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.transaction.support.TransactionTemplate
import java.time.Instant

/**
 * @author petrique
 */
class ForecastChecker(
    private val forecastClient: ForecastClient,
    private val telegramClient: TelegramClient,
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
            transactionTemplate.execute {
                diff.forEach {
                    if (it.isNew()) {
                        forecastDao.insert(
                            Forecast(forecastDao.generateForecastId(), it.epochTime, it.clouds)
                        )
                    } else {
                        forecastDao.updateClouds(Forecast(it.id!!, it.epochTime, it.clouds))
                    }
                }
            }

            val message = diff.asSequence()
                .map { Instant.ofEpochSecond(it.epochTime) }
                .joinToString(separator = "; ", prefix = "sunny time=") { it.toString() }

            log.info("there will be a sunny day")
            subscriptionDao.selectAll().forEach {
                telegramClient.sendMessage(chatId = it.chatId, text = message)
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