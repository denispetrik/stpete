package den.ptrq.stpete.subscription

import den.ptrq.stpete.jooq.Sequences.SEQ_SUBSCRIPTION_ID
import den.ptrq.stpete.jooq.Tables.SUBSCRIPTION
import den.ptrq.stpete.jooq.tables.records.SubscriptionRecord
import org.jooq.DSLContext
import org.jooq.RecordMapper
import org.slf4j.LoggerFactory

/**
 * @author petrique
 */
class SubscriptionDao(private val context: DSLContext) {

    fun generateSubscriptionId(): Long {
        log.info("generateSubscriptionId()")
        return context.nextval(SEQ_SUBSCRIPTION_ID).toLong()
    }

    fun insert(subscription: Subscription) {
        log.info("insert({})", subscription)
        context
            .insertInto(SUBSCRIPTION)
            .set(SUBSCRIPTION.ID, subscription.id)
            .set(SUBSCRIPTION.USER_ID, subscription.userId)
            .set(SUBSCRIPTION.USER_NAME, subscription.userName)
            .set(SUBSCRIPTION.CHAT_ID, subscription.chatId)
            .set(SUBSCRIPTION.CHAT_TYPE, subscription.chatType)
            .execute()
    }

    fun selectAllSubscriptions(): List<Subscription> {
        log.info("selectAllSubscriptions()")
        return context
            .selectFrom(SUBSCRIPTION)
            .fetch(mapper)
    }

    companion object {
        private val log = LoggerFactory.getLogger(SubscriptionDao::class.java)
        private val mapper = RecordMapper<SubscriptionRecord, Subscription> { record ->
            Subscription(
                id = record.id,
                userId = record.userId,
                userName = record.userName,
                chatId = record.chatId,
                chatType = record.chatType
            )
        }
    }
}