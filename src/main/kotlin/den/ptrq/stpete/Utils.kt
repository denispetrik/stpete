package den.ptrq.stpete

import java.time.LocalDate

/**
 * @author petrique
 */

const val TEN_SECONDS = 10_000L
const val THIRTY_SECONDS = 30_000L
const val TEN_MINUTES = 600_000L

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

fun monthName(date: LocalDate) = monthNames[date.monthValue]
