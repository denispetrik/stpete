package den.ptrq.stpete

import den.ptrq.stpete.jooq.Sequences.SEQ_SUBSCRIPTION_ID
import den.ptrq.stpete.jooq.Tables.SUBSCRIPTION
import org.jooq.DSLContext

/**
 * @author petrique
 */

class Subscription(
    val id: Long,
    val userId: Long,
    val userName: String,
    val chatId: Long,
    val chatType: String
)

class SubscriptionDao(private val context: DSLContext) {

    fun generateSubscriptionId(): Long = context.nextval(SEQ_SUBSCRIPTION_ID).toLong()

    fun insert(subscription: Subscription) {
        context
            .insertInto(SUBSCRIPTION)
            .set(SUBSCRIPTION.ID, subscription.id)
            .set(SUBSCRIPTION.USER_ID, subscription.userId)
            .set(SUBSCRIPTION.USER_NAME, subscription.userName)
            .set(SUBSCRIPTION.CHAT_ID, subscription.chatId)
            .set(SUBSCRIPTION.CHAT_TYPE, subscription.chatType)
            .execute()
    }
}
