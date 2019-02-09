package den.ptrq.stpete.interaction

import java.time.LocalDateTime

/**
 * @author petrique
 */
class Interaction(
    val id: Long,
    val updateId: Long,
    val userId: Long,
    val userName: String,
    val chatId: Long,
    val chatType: String,
    val dateTime: LocalDateTime,
    val text: String
) {
    override fun toString(): String {
        return "Interaction(id=$id, updateId=$updateId, userId=$userId, userName='$userName', chatId=$chatId, " +
                "chatType='$chatType', dateTime=$dateTime, text='$text')"
    }
}
