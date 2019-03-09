package den.ptrq.stpete.forecast

/**
 * @author petrique
 */
class ForecastMessageCreator {

    fun createSunnyDaysMessage(forecastList: List<Forecast>): String {
        return forecastList.asSequence()
            .map { it.dateTime }
            .groupBy({ it.dayOfMonth }, { it.hour })
            .map { (dayOfMonth, hours) -> sunnyDayText(dayOfMonth, hours) }
            .joinToString("\n")
    }
}

private fun sunnyDayText(dayOfMonth: Int, hours: List<Int>): String {
    val periodsString = splitToPeriods(hours).asSequence()
        .map { "${it.first}-${it.second}" }
        .joinToString(", ")
    return "$dayOfMonth, periods: $periodsString"
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
