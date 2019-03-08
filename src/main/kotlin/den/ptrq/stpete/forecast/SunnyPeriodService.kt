package den.ptrq.stpete.forecast

/**
 * @author petrique
 */
class SunnyPeriodService {

    fun isSunny(forecast: Forecast) = forecast.clouds <= 20

    fun filterSunny(forecasts: List<Forecast>): List<Forecast> =
        forecasts.filter { isSunny(it) }

    fun isDifferent(newForecasts: List<Forecast>, oldForecasts: List<Forecast>): Boolean {
        val oldForecastMap = oldForecasts.associateBy { it.epochTime }
        return newForecasts.any { it.differsFrom(oldForecastMap[it.epochTime]) }
    }

    private fun Forecast.differsFrom(another: Forecast?): Boolean =
        another == null || isSunny(this) != isSunny(another)
}
