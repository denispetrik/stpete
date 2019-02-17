package den.ptrq.stpete.telegram

import den.ptrq.stpete.get
import den.ptrq.stpete.post
import org.springframework.web.client.RestTemplate

/**
 * @author petrique
 */
class TelegramClient(private val restTemplate: RestTemplate) {

    private val token = "788663904:AAGT4qBz8LFQ1SfytalE5Z5d9t3C4BcndsM"
    private val baseUrl = "https://api.telegram.org/bot$token"

    fun getMe(): Response<User> {
        return restTemplate
            .get<Response<User>>("$baseUrl/getMe")
            .getValueOrElse { throw RuntimeException("getMe call failed with error ${getError()}") }
    }

    fun getUpdates(offset: Long, limit: Int): Response<List<Update>> {
        val request = GetUpdatesRequest(offset, limit, timeout = 0)
        return restTemplate
            .post<Response<List<Update>>>("$baseUrl/getUpdates", request)
            .getValueOrElse { throw RuntimeException("getUpdates call failed with error ${getError()}") }
    }

    fun sendMessage(chatId: Long, text: String): Response<Message> {
        val request = SendMessageRequest(chatId, text)
        return restTemplate
            .post<Response<Message>>("$baseUrl/sendMessage", request)
            .getValueOrElse { throw RuntimeException("sendMessage call failed with error ${getError()}") }
    }
}
