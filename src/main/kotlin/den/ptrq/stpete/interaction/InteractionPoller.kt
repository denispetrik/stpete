package den.ptrq.stpete.interaction

import den.ptrq.stpete.TEN_MINUTES
import den.ptrq.stpete.THIRTY_SECONDS
import den.ptrq.stpete.common.ChatType
import den.ptrq.stpete.telegram.TelegramClient
import den.ptrq.stpete.telegram.Update
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

    @Scheduled(fixedRate = TEN_MINUTES, initialDelay = THIRTY_SECONDS)
    fun pollInteractions() {
        log.info("polling new bot interactions")

        val latestUpdateId = interactionDao.selectLatest().map { it.updateId }.orElse(0)
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

    private fun Update.toInteraction(): Interaction {
        val keyWords = extractBotCommands().asSequence()
            .map { KeyWord(type = KeyWord.Type.BOT_COMMAND, value = it) }
            .toList()

        return Interaction(
            id = interactionDao.generateInteractionId(),
            updateId = id,
            userId = message.user.id,
            userName = message.user.userName,
            chatId = message.chat.id,
            chatType = ChatType.byCode(message.chat.type),
            dateTime = LocalDateTime.ofEpochSecond(message.dateTime, 0, ZoneOffset.UTC),
            text = message.text,
            keyWords = keyWords,
            processed = false
        )
    }

    companion object {
        private val log = LoggerFactory.getLogger(InteractionPoller::class.java)
    }
}
