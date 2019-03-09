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

        return newForecasts.any { newForecast ->
            val oldForecast = oldForecastMap[newForecast.epochTime]
            if (oldForecast == null) {
                isSunny(newForecast)
            } else {
                isSunny(newForecast) != isSunny(oldForecast)
            }
        }
    }

    private fun isSunny(forecast: Forecast): Boolean =
        forecast.dateTime.inBetween(startOfDay, endOfDay) && forecast.clouds <= 20
}
