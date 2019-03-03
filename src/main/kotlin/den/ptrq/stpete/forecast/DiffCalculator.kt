package den.ptrq.stpete.forecast

import org.slf4j.LoggerFactory

/**
 * @author petrique
 */
class DiffCalculator {

    fun calculateDiff(newForecasts: List<Forecast>, oldForecasts: List<Forecast>): List<Forecast> {
        val oldForecastMap = oldForecasts.associateBy { it.epochTime }
        return newForecasts
            .filter { it.differsFrom(oldForecastMap[it.epochTime]) }
            .apply { log.info("diff={}", this) }
    }

    private fun Forecast.differsFrom(another: Forecast?): Boolean =
        if (another == null) {
            this.isSunny()
        } else {
            this.isSunny() != another.isSunny()
        }

    private fun Forecast.isSunny() = clouds <= 20

    companion object {
        private val log = LoggerFactory.getLogger(DiffCalculator::class.java)
    }
}
