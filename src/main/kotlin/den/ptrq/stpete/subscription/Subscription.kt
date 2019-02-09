package den.ptrq.stpete.subscription

/**
 * @author petrique
 */
class Subscription(
    val id: Long,
    val userId: Long,
    val userName: String,
    val chatId: Long,
    val chatType: String
) {
    override fun toString(): String {
        return "Subscription(id=$id, userId=$userId, userName='$userName', chatId=$chatId, chatType='$chatType')"
    }
}
