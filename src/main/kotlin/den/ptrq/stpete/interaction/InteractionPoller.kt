package den.ptrq.stpete.interaction

import den.ptrq.stpete.client.telegram.TelegramClient
import den.ptrq.stpete.client.telegram.Update
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.transaction.support.TransactionTemplate
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 * @author petrique
 */
class InteractionPoller(
    private val telegramClient: TelegramClient,
    private val transactionTemplate: TransactionTemplate,
    private val interactionDao: InteractionDao
) {

    @Scheduled(fixedRate = 10000, initialDelay = 10000)
    fun pollInteractions() {
        log.info("polling new bot interactions")

        val latestUpdateId = interactionDao.selectLatestInteraction().map { it.updateId }.orElse(0)
        log.info("latest updateId={}", latestUpdateId)

        val response = telegramClient.getUpdates(offset = latestUpdateId + 1, limit = 3)
        if (!response.ok) {
            throw RuntimeException("telegram bot api call failed")
        }

        val updates = response.result
        if (updates.isEmpty()) {
            log.info("no interaction has been received")
            return
        }

        transactionTemplate.execute {
            updates.asSequence()
                .map { it.toInteraction() }
                .forEach { interactionDao.insert(it) }
        }
    }

    private fun Update.toInteraction() = Interaction(
        id = interactionDao.generateInteractionId(),
        updateId = id,
        userId = message.user.id,
        userName = message.user.userName,
        chatId = message.chat.id,
        chatType = message.chat.type,
        dateTime = LocalDateTime.ofEpochSecond(message.dateTime, 0, ZoneOffset.UTC),
        text = message.text
    )

    companion object {
        private val log = LoggerFactory.getLogger(InteractionPoller::class.java)
    }
}
