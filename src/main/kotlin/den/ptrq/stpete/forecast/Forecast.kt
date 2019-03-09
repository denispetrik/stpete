package den.ptrq.stpete.forecast

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * @author petrique
 */
class Forecast(
    val id: Long,
    val epochTime: Long,
    val clouds: Int
) {
    val dateTime: ZonedDateTime
        get() = ZonedDateTime.ofInstant(Instant.ofEpochSecond(this.epochTime), ZoneId.of("+3"))


    override fun toString(): String {
        return "Forecast(id=$id, epochTime=$epochTime, clouds=$clouds)"
    }
}
