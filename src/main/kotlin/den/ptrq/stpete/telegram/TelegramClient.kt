package den.ptrq.stpete.telegram

import den.ptrq.stpete.MockableInTests
import den.ptrq.stpete.post
import org.springframework.web.client.RestTemplate

/**
 * @author petrique
 */
@MockableInTests
class TelegramClient(
    private val restTemplate: RestTemplate,
    token: String
) {
    private val baseUrl = "https://api.telegram.org/bot$token"

    fun getUpdates(offset: Long, limit: Int): List<Update> {
        val request = GetUpdatesRequest(offset, limit, timeout = 0)
        val response = restTemplate.post<TelegramResponse<List<Update>>>("$baseUrl/getUpdates", request)
        return with(response) {
            if (ok) result else throw RuntimeException("getUpdates call failed")
        }
    }

    fun sendMessage(chatId: Long, text: String): Message {
        val request = SendMessageRequest(chatId, text)
        val response = restTemplate.post<TelegramResponse<Message>>("$baseUrl/sendMessage", request)
        return with(response) {
            if (ok) result else throw RuntimeException("sendMessage call failed")
        }
    }
}
