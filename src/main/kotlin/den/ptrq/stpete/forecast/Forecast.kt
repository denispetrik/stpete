package den.ptrq.stpete.forecast

/**
 * @author petrique
 */
class Forecast(
    val id: Long,
    val epochTime: Long,
    val clouds: Int
) {
    override fun toString(): String {
        return "Forecast(id=$id, epochTime=$epochTime, clouds=$clouds)"
    }
}
