package den.ptrq.stpete.notification

/**
 * @author petrique
 */
class Notification(
    val id: Long,
    val status: Status,
    val chatId: Long,
    val message: String
) {
    override fun toString(): String {
        return "Notification(id=$id, status=$status, chatId=$chatId, message='$message')"
    }


    enum class Status(val code: String) {
        NEW("new"),
        SENT("sent");

        companion object {
            fun byCode(code: String): Status = values().first { it.code == code }
        }
    }
}
