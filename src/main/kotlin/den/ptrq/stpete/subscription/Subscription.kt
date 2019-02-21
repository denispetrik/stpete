package den.ptrq.stpete.subscription

import den.ptrq.stpete.common.ChatType

/**
 * @author petrique
 */
class Subscription(
    val id: Long,
    val userId: Long,
    val userName: String,
    val chatId: Long,
    val chatType: ChatType
) {
    override fun toString(): String {
        return "Subscription(id=$id, userId=$userId, userName='$userName', chatId=$chatId, chatType=$chatType)"
    }
}
