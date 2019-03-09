package den.ptrq.stpete.forecast

import den.ptrq.stpete.TEN_MINUTES
import den.ptrq.stpete.THIRTY_SECONDS
import den.ptrq.stpete.notification.NotificationSender
import den.ptrq.stpete.subscription.SubscriptionDao
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.transaction.support.TransactionTemplate

/**
 * @author petrique
 */
class ForecastChecker(
    private val forecastClient: ForecastClient,
    private val sunnyPeriodService: SunnyPeriodService,
    private val forecastMessageCreator: ForecastMessageCreator,
    private val notificationSender: NotificationSender,
    private val transactionTemplate: TransactionTemplate,
    private val forecastDao: ForecastDao,
    private val subscriptionDao: SubscriptionDao
) {

    @Scheduled(fixedRate = TEN_MINUTES, initialDelay = THIRTY_SECONDS)
    fun checkForecast() {
        log.info("checking forecast")

        val newForecastItems = forecastClient.getForecast()
        val oldForecasts = forecastDao.selectActual()

        transactionTemplate.execute {
            val newForecasts = upsertAll(newForecastItems, oldForecasts)

            if (sunnyPeriodService.isDifferent(newForecasts, oldForecasts)) {
                val sunnyForecasts = sunnyPeriodService.filterSunny(newForecasts)
                val message = forecastMessageCreator.createSunnyDaysMessage(sunnyForecasts)
                subscriptionDao.selectAll().forEach {
                    notificationSender.sendAsynchronously(it.chatId, message)
                }
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

    companion object {
        private val log = LoggerFactory.getLogger(ForecastChecker::class.java)
    }
}
