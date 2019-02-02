package den.ptrq.stpete.client.telegram

/**
 * @author petrique
 */

class CommandParser {

    fun parse(update: Update): List<String> {
        val message = update.message ?: throw RuntimeException()
        val text = message.text ?: throw RuntimeException()
        val entities = message.entities ?: throw RuntimeException()

        return entities.asSequence()
            .filter { it.type == "bot_command" }
            .map { entity -> text.substring(entity.offset, entity.length) }
            .toList()
    }
}
