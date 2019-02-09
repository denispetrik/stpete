package den.ptrq.stpete

import den.ptrq.stpete.client.ForecastClient
import den.ptrq.stpete.client.telegram.TelegramClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * @author petrique
 */

@Configuration
class FacadeConfiguration {
    @Bean
    fun facadeController(telegramClient: TelegramClient, forecastClient: ForecastClient) =
        FacadeController(telegramClient, forecastClient)
}

@RestController
class FacadeController(
    private val telegramClient: TelegramClient,
    private val forecastClient: ForecastClient
) {

    @GetMapping("/bot/info")
    fun info(): String {
        val response = telegramClient.getMe()
        return with(response.result) {
            "id=$id, firstName=$firstName, userName=$userName"
        }
    }

    @GetMapping("/bot/updates")
    fun updates(): String {
        val response = telegramClient.getUpdates(offset = 0, limit = 3)
        return response.result.asSequence()
            .map { it.message }
            .joinToString(separator = ";") { "id=${it.id}, text=${it.text}" }
    }

    @GetMapping("/bot/send")
    fun send(@RequestParam chatId: Long, @RequestParam text: String): String {
        val response = telegramClient.sendMessage(chatId, text)
        return with(response.result) {
            "id=$id, text=$text"
        }
    }

    @GetMapping("/forecast")
    fun forecast(): String {
        val response = forecastClient.getForecast()
        return response.forecastItems.asSequence()
            .mapNotNull { "date=${it.date}, clouds=${it.clouds.percentage}" }
            .joinToString()
    }
}
