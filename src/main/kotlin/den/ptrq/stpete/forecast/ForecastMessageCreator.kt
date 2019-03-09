package den.ptrq.stpete.forecast

import den.ptrq.stpete.monthName
import java.time.LocalDate

/**
 * @author petrique
 */
class ForecastMessageCreator {

    fun createSunnyDaysMessage(forecastList: List<Forecast>): String {
        return forecastList.asSequence()
            .map { it.dateTime }
            .groupBy({ it.toLocalDate() }, { it.hour })
            .map { (date, hours) -> sunnyDayText(date, hours) }
            .joinToString("\n")
    }

    private fun sunnyDayText(date: LocalDate, hours: List<Int>): String {
        val monthName = monthName(date)

        val periodsString = splitToPeriods(hours).asSequence()
            .map { "${it.first}-${it.second}" }
            .joinToString(", ")

        return "${date.dayOfMonth} $monthName: $periodsString"
    }
}

fun splitToPeriods(hours: List<Int>): List<Pair<Int, Int>> {
    if (hours.isEmpty()) {
        return emptyList()
    }
    val periods = mutableListOf<Pair<Int, Int>>()
    var begin = hours[0]
    var end = hours[0]
    hours.sorted().forEach { currentHour ->
        if (currentHour - end > 3) {
            periods += Pair(begin, end + 3)
            begin = currentHour
            end = currentHour
        } else {
            end = currentHour
        }
    }
    periods += Pair(begin, end + 3)
    return periods
}
