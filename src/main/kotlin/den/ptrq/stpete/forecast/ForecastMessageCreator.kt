package den.ptrq.stpete.forecast

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
        val monthName = monthNames[date.monthValue]

        val periodsString = splitToPeriods(hours).asSequence()
            .sorted()
            .map { it.text }
            .joinToString(", ")

        return "${date.dayOfMonth} $monthName: $periodsString"
    }
}

enum class Period(private val range: IntRange, val text: String) {
    MORNING(6..11, "утро"),
    DAYTIME(12..17, "день"),
    EVENING(18..22, "вечер");

    companion object {
        fun byHour(hour: Int): Period =
            values().find { hour in it.range } ?: throw RuntimeException("unsupported hour")
    }
}

fun splitToPeriods(hours: List<Int>): Set<Period> = hours.map { Period.byHour(it) }.toSet()

private val monthNames = mapOf(
    1 to "января",
    2 to "февраля",
    3 to "марта",
    4 to "апреля",
    5 to "мая",
    6 to "июня",
    7 to "июля",
    8 to "августа",
    9 to "сентября",
    10 to "октября",
    11 to "ноября",
    12 to "декабря"
)
