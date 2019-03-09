package den.ptrq.stpete.forecast

import den.ptrq.stpete.inBetween
import java.time.LocalTime

/**
 * @author petrique
 */
class SunnyPeriodService(
    private val startOfDay: LocalTime,
    private val endOfDay: LocalTime
) {

    fun filterSunny(forecasts: List<Forecast>) = forecasts.filter { isSunny(it) }

    fun isDifferent(newForecasts: List<Forecast>, oldForecasts: List<Forecast>): Boolean {
        val oldForecastMap = oldForecasts.associateBy { it.epochTime }
        return newForecasts.any { it.differsFrom(oldForecastMap[it.epochTime]) }
    }

    private fun isSunny(forecast: Forecast): Boolean =
        forecast.dateTime.inBetween(startOfDay, endOfDay) && forecast.clouds <= 20

    private fun Forecast.differsFrom(another: Forecast?): Boolean =
        another == null || isSunny(this) != isSunny(another)
}
