package den.ptrq.stpete.client.telegram

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author petrique
 */

class GetUpdatesRequest(
    @JsonProperty("offset") val offset: Int,
    @JsonProperty("limit") val limit: Int,
    @JsonProperty("timeout") val timeout: Int
)

class SendMessageRequest(
    @JsonProperty("chat_id") val chatId: Long,
    @JsonProperty("text") val text: String
)

class Response<T>(
    @JsonProperty("ok") val ok: Boolean,
    @JsonProperty("result") val result: T
)

class Update(
    @JsonProperty("update_id") val id: Long,
    @JsonProperty("message") val message: Message?
)

class Message(
    @JsonProperty("message_id") val id: Long,
    @JsonProperty("from") val from: User?,
    @JsonProperty("date") val date: Int,
    @JsonProperty("chat") val chat: Chat,
    @JsonProperty("text") val text: String?,
    @JsonProperty("entities") val entities: List<MessageEntity>?
)

class User(
    @JsonProperty("id") val id: Long,
    @JsonProperty("is_bot") val isBot: Boolean,
    @JsonProperty("first_name") val firstName: String,
    @JsonProperty("last_name") val lastName: String?,
    @JsonProperty("username") val userName: String?
)

class Chat(
    @JsonProperty("id") val id: Long,
    @JsonProperty("type") val type: String, //todo enum: “private”, “group”, “supergroup” or “channel”
    @JsonProperty("title") val title: String?,
    @JsonProperty("username") val userName: String?
)

class MessageEntity(
    //mention (@username), hashtag, cashtag, bot_command, url, email, phone_number, bold (bold text),
    //italic (italic text), code (monowidth string), pre (monowidth block), text_link (for clickable text URLs),
    //text_mention (for users without usernames)
    @JsonProperty("type") val type: String,
    @JsonProperty("offset") val offset: Int,
    @JsonProperty("length") val length: Int
)
