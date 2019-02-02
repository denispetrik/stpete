package den.ptrq.stpete.client.telegram

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * @author petrique
 */

class CommandParserTests {

    @Test
    fun `should parse commands`() {
        val entity = MessageEntity(type = "bot_command", offset = 0, length = 6)
        val chat = Chat(id = 1L, type = "private", title = null, userName = null)
        val message = Message(id = 1L, from = null, date = 123, chat = chat, text = "/start", entities = listOf(entity))
        val update = Update(1L, message = message)

        val commandParser = CommandParser()
        val commands = commandParser.parse(update)

        assertThat(commands).hasSize(1)
        assertThat(commands).contains("/start")
    }
}