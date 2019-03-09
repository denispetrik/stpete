package den.ptrq.stpete.subscription

import den.ptrq.stpete.common.ChatType
import den.ptrq.stpete.interaction.InteractionDao
import den.ptrq.stpete.interaction.InteractionPoller
import den.ptrq.stpete.interaction.InteractionProcessor
import den.ptrq.stpete.interaction.KeyWord
import den.ptrq.stpete.telegram.*
import den.ptrq.stpete.test.IntTests
import den.ptrq.stpete.test.TestUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 * @author petrique
 */
class SubscriptionTests : IntTests() {

    @Autowired
    lateinit var testUtils: TestUtils

    @Autowired
    lateinit var interactionDao: InteractionDao
    @Autowired
    lateinit var interactionPoller: InteractionPoller
    @Autowired
    lateinit var interactionProcessor: InteractionProcessor
    @Autowired
    lateinit var subscriptionDao: SubscriptionDao

    @MockBean
    lateinit var telegramClient: TelegramClient

    @Test
    fun `should upload interaction and save it as subscription`() {
        testUtils.insertNewInteraction()

        val update = createUpdate()

        `when`(telegramClient.getUpdates(anyLong(), anyInt())).thenReturn(listOf(update))

        interactionPoller.pollInteractions()

        val interaction = interactionDao.selectByUpdateId(update.id)

        assertThat(interaction.updateId).isEqualTo(update.id)
        assertThat(interaction.userId).isEqualTo(update.message.user.id)
        assertThat(interaction.userName).isEqualTo(update.message.user.userName)
        assertThat(interaction.chatId).isEqualTo(update.message.chat.id)
        assertThat(interaction.chatType).isEqualTo(ChatType.byCode(update.message.chat.type))
        assertThat(interaction.dateTime)
            .isEqualTo(LocalDateTime.ofEpochSecond(update.message.dateTime, 0, ZoneOffset.UTC))
        assertThat(interaction.text).isEqualTo(update.message.text)
        assertThat(interaction.keyWords).hasSize(1)
        assertThat(interaction.keyWords[0].type).isEqualTo(KeyWord.Type.BOT_COMMAND)
        assertThat(interaction.keyWords[0].value).isEqualTo("start")

        interactionProcessor.processInteractions()

        val subscription = subscriptionDao.selectByUserId(interaction.userId)

        assertThat(subscription.userId).isEqualTo(interaction.userId)
        assertThat(subscription.userName).isEqualTo(interaction.userName)
        assertThat(subscription.chatId).isEqualTo(interaction.chatId)
        assertThat(subscription.chatType).isEqualTo(interaction.chatType)
    }

    private fun createUpdate(): Update {
        val user = User(
            id = testUtils.generateLong(),
            firstName = "firstName",
            lastName = "lastName",
            userName = "userName"
        )
        val chat = Chat(id = testUtils.generateLong(), type = "private", title = null, userName = null)
        val message = Message(
            id = testUtils.generateLong(),
            user = user,
            dateTime = Instant.now().epochSecond,
            chat = chat,
            text = "/start",
            entities = listOf(MessageEntity(type = "bot_command", offset = 0, length = 6))
        )
        return Update(id = testUtils.generateLong(), message = message)
    }
}
