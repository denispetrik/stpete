package den.ptrq.stpete.interaction

import den.ptrq.stpete.common.ChatType.PRIVATE
import den.ptrq.stpete.test.IntTests
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.support.TransactionTemplate
import java.time.LocalDateTime

/**
 * @author petrique
 */
class InteractionDaoTests : IntTests() {

    @Autowired
    lateinit var transactionTemplate: TransactionTemplate
    @Autowired
    lateinit var interactionDao: InteractionDao

    @Test
    fun `should save interaction`() {
        val interaction = Interaction(
            id = interactionDao.generateInteractionId(),
            updateId = 1,
            userId = 2,
            userName = "userName",
            chatId = 3,
            chatType = PRIVATE,
            dateTime = LocalDateTime.now(),
            text = "/start",
            keyWords = listOf(KeyWord(type = KeyWord.Type.BOT_COMMAND, value = "start")),
            processed = false
        )

        transactionTemplate.execute {
            interactionDao.insert(interaction)
        }

        val inserted = interactionDao.selectById(interaction.id)

        assertThat(inserted.id).isEqualTo(interaction.id)
        assertThat(inserted.updateId).isEqualTo(interaction.updateId)
        assertThat(inserted.userId).isEqualTo(interaction.userId)
        assertThat(inserted.userName).isEqualTo(interaction.userName)
        assertThat(inserted.chatId).isEqualTo(interaction.chatId)
        assertThat(inserted.chatType).isEqualTo(interaction.chatType)
        assertThat(inserted.dateTime).isEqualTo(interaction.dateTime)
        assertThat(inserted.text).isEqualTo(interaction.text)
        assertThat(inserted.keyWords).hasSize(1)
        assertThat(inserted.keyWords[0]).isEqualToComparingFieldByField(interaction.keyWords[0])
        assertThat(inserted.processed).isEqualTo(interaction.processed)
    }
}
