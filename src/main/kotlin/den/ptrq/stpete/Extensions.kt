package den.ptrq.stpete

import java.time.LocalTime
import java.time.ZonedDateTime

/**
 * @author petrique
 */

fun ZonedDateTime.inBetween(startOfDay: LocalTime, endOfDay: LocalTime): Boolean =
    this.toLocalTime().let {
        it.isAfter(startOfDay) && it.isBefore(endOfDay)
    }
