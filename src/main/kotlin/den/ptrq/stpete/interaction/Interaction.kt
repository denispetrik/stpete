package den.ptrq.stpete.interaction

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import den.ptrq.stpete.common.ChatType
import java.time.LocalDateTime

/**
 * @author petrique
 */
class Interaction(
    val id: Long,
    val updateId: Long,
    val userId: Long,
    val userName: String,
    val chatId: Long,
    val chatType: ChatType,
    val dateTime: LocalDateTime,
    val text: String,
    val keyWords: List<KeyWord>,
    val processed: Boolean
) {
    override fun toString(): String {
        return "Interaction(id=$id, updateId=$updateId, userId=$userId, userName='$userName', " +
                "chatId=$chatId, chatType=$chatType, dateTime=$dateTime, text='$text', " +
                "keyWords=$keyWords, processed=$processed)"
    }
}

class KeyWord(
    @JsonProperty("type") val type: Type,
    @JsonProperty("value") val value: String
) {
    enum class Type(@JsonValue val code: String) {
        BOT_COMMAND("botCommand")
    }

    override fun toString(): String {
        return "KeyWord(type=$type, value='$value')"
    }
}
