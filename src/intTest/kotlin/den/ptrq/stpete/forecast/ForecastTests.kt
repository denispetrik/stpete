package den.ptrq.stpete.forecast

import den.ptrq.stpete.notification.Notification
import den.ptrq.stpete.notification.NotificationDao
import den.ptrq.stpete.notification.NotificationSender
import den.ptrq.stpete.telegram.TelegramClient
import den.ptrq.stpete.test.IntTests
import den.ptrq.stpete.test.TestUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import java.time.ZonedDateTime

/**
 * @author petrique
 */
class ForecastTests : IntTests() {

    @Autowired
    lateinit var testUtils: TestUtils

    @Autowired
    lateinit var forecastDao: ForecastDao
    @Autowired
    lateinit var notificationDao: NotificationDao
    @Autowired
    lateinit var forecastChecker: ForecastChecker
    @Autowired
    lateinit var forecastMessageCreator: ForecastMessageCreator
    @Autowired
    lateinit var notificationSender: NotificationSender

    @MockBean
    lateinit var forecastClient: ForecastClient
    @MockBean
    lateinit var telegramClient: TelegramClient

    @Test
    fun `should get fresh forecast and send notification`() {
        val subscription = testUtils.insertNewSubscription()

        val cloudyForecastEpochTime = ZonedDateTime.now().plusHours(1).toEpochSecond()
        testUtils.insertNewForecast(epochTime = cloudyForecastEpochTime, clouds = 80)

        val sunnyForecastEpochTime = ZonedDateTime.now().plusHours(2).toEpochSecond()
        testUtils.insertNewForecast(epochTime = sunnyForecastEpochTime, clouds = 0)

        val newForecastEpochTime = ZonedDateTime.now().plusHours(3).toEpochSecond()

        val forecastItems = listOf(
            ForecastItem(cloudyForecastEpochTime, Clouds(percentage = 0)),
            ForecastItem(sunnyForecastEpochTime, Clouds(percentage = 0)),
            ForecastItem(newForecastEpochTime, Clouds(percentage = 0))
        )

        `when`(forecastClient.getForecast()).thenReturn(forecastItems)

        forecastChecker.checkForecast()

        val forecasts = forecastDao.selectActual()
        val forecastMap = forecasts.associateBy { it.epochTime }
        val updatedForecast = forecastMap[cloudyForecastEpochTime]!!
        val notUpdatedForecast = forecastMap[sunnyForecastEpochTime]!!
        val newForecast = forecastMap[newForecastEpochTime]!!
        val message = forecastMessageCreator.createSunnyDaysMessage(forecasts)

        assertThat(updatedForecast.clouds).isEqualTo(0)
        assertThat(notUpdatedForecast.clouds).isEqualTo(0)
        assertThat(newForecast.clouds).isEqualTo(0)

        val notification = notificationDao.selectAll().first { it.chatId == subscription.chatId }

        assertThat(notification.status).isEqualTo(Notification.Status.NEW)
        assertThat(notification.message).isEqualTo(message)

        notificationSender.sendNewNotifications()

        val sentNotification = notificationDao.selectById(notification.id)

        assertThat(sentNotification.status).isEqualTo(Notification.Status.SENT)

        verify(telegramClient).sendMessage(subscription.chatId, message)
    }
}
