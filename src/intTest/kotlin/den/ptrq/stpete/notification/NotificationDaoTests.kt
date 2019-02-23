package den.ptrq.stpete.notification

import den.ptrq.stpete.test.IntTests
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.support.TransactionTemplate

/**
 * @author petrique
 */
class NotificationDaoTests : IntTests() {

    @Autowired
    lateinit var transactionTemplate: TransactionTemplate
    @Autowired
    lateinit var notificationDao: NotificationDao

    @Test
    fun `should save notification`() {
        val notification = Notification(
            id = notificationDao.generateNotificationId(),
            status = Notification.Status.NEW,
            chatId = 1,
            message = "message"
        )

        transactionTemplate.execute {
            notificationDao.insert(notification)
        }

        val inserted = notificationDao.selectById(notification.id)

        assertThat(inserted.id).isEqualTo(notification.id)
        assertThat(inserted.status).isEqualTo(notification.status)
        assertThat(inserted.chatId).isEqualTo(notification.chatId)
        assertThat(inserted.message).isEqualTo(notification.message)
    }
}
