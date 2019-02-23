package den.ptrq.stpete.notification

import den.ptrq.stpete.jooq.Sequences.SEQ_NOTIFICATION_ID
import den.ptrq.stpete.jooq.Tables.NOTIFICATION
import den.ptrq.stpete.jooq.tables.records.NotificationRecord
import org.jooq.DSLContext
import org.jooq.RecordMapper
import org.slf4j.LoggerFactory

/**
 * @author petrique
 */
class NotificationDao(private val context: DSLContext) {

    fun generateNotificationId(): Long {
        log.info("generateNotificationId()")
        return context.nextval(SEQ_NOTIFICATION_ID).toLong()
    }

    fun insert(notification: Notification) {
        log.info("insert({})", notification)
        context
            .insertInto(NOTIFICATION)
            .set(NOTIFICATION.ID, notification.id)
            .set(NOTIFICATION.STATUS, notification.status.code)
            .set(NOTIFICATION.CHAT_ID, notification.chatId)
            .set(NOTIFICATION.MESSAGE, notification.message)
            .execute()
    }

    fun markAsSent(notification: Notification) {
        log.info("markAsSent({})", notification)
        context
            .update(NOTIFICATION)
            .set(NOTIFICATION.STATUS, Notification.Status.SENT.code)
            .where(NOTIFICATION.ID.equal(notification.id))
            .execute()
    }

    fun selectById(id: Long): Notification {
        log.info("selectById(id={})", id)
        return context
            .selectFrom(NOTIFICATION)
            .where(NOTIFICATION.ID.equal(id))
            .fetchOne(mapper)
    }

    fun selectNew(limit: Int): List<Notification> {
        log.info("selectNew(limit={})", limit)
        return context
            .selectFrom(NOTIFICATION)
            .where(NOTIFICATION.STATUS.equal(Notification.Status.NEW.code))
            .orderBy(NOTIFICATION.DATE_TIME.asc())
            .limit(limit)
            .fetch(mapper)
    }

    companion object {
        private val log = LoggerFactory.getLogger(NotificationDao::class.java)
        private val mapper = RecordMapper<NotificationRecord, Notification> { record ->
            Notification(
                id = record.id,
                status = Notification.Status.byCode(record.status),
                chatId = record.chatId,
                message = record.message
            )
        }
    }
}