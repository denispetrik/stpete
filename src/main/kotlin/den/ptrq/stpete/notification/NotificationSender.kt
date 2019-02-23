package den.ptrq.stpete.notification

import den.ptrq.stpete.telegram.TelegramClient
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.transaction.support.TransactionTemplate

/**
 * @author petrique
 */
class NotificationSender(
    private val telegramClient: TelegramClient,
    private val transactionTemplate: TransactionTemplate,
    private val notificationDao: NotificationDao
) {

    @Scheduled(fixedRate = 10000, initialDelay = 10000)
    fun sendNewNotifications() {
        log.info("sending new notifications")
        notificationDao.selectNew(limit = 3).forEach { send(it) }
    }

    private fun send(notification: Notification) {
        log.info("send({})", notification)

        telegramClient.sendMessage(chatId = notification.chatId, text = notification.message)

        transactionTemplate.execute {
            notificationDao.markAsSent(notification)
        }
    }

    fun sendAsynchronously(chatId: Long, message: String) {
        log.info("sendAsynchronously(chatId={}, message={})", chatId, message)
        transactionTemplate.execute {
            notificationDao.insert(
                Notification(
                    id = notificationDao.generateNotificationId(),
                    status = Notification.Status.NEW,
                    chatId = chatId,
                    message = message
                )
            )
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(NotificationSender::class.java)
    }
}
