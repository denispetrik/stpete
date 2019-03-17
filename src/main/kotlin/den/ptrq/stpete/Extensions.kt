package den.ptrq.stpete

import java.time.LocalTime
import java.time.ZonedDateTime

/**
 * @author petrique
 */

fun ZonedDateTime.inBetween(from: LocalTime, to: LocalTime): Boolean =
    this.toLocalTime().let {
        it.isAfter(from) && it.isBefore(to) || it == from || it == to
    }
