package den.ptrq.stpete.client.telegram

import den.ptrq.stpete.telegram.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * @author petrique
 */

class ApiTests {
    @Test
    fun `should parse commands`() {
        val user = User(id = 1, firstName = "firstName", lastName = "lastName", userName = "userName")
        val chat = Chat(id = 1, type = "private", title = null, userName = null)
        val text = "foo /start bar /stop"
        val entities = listOf(
            MessageEntity(type = "bot_command", offset = 4, length = 6),
            MessageEntity(type = "bot_command", offset = 15, length = 5)
        )
        val message = Message(
            id = 1,
            user = user,
            dateTime = 123,
            chat = chat,
            text = text,
            entities = entities
        )
        val update = Update(id = 1, message = message)

        val commands = update.extractBotCommands()

        assertThat(commands).hasSize(2)
        assertThat(commands).contains("start", "stop")
    }
}
