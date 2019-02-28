package den.ptrq.stpete.test

import den.ptrq.stpete.common.ChatType
import den.ptrq.stpete.forecast.Forecast
import den.ptrq.stpete.forecast.ForecastDao
import den.ptrq.stpete.interaction.Interaction
import den.ptrq.stpete.interaction.InteractionDao
import den.ptrq.stpete.subscription.Subscription
import den.ptrq.stpete.subscription.SubscriptionDao
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.support.TransactionTemplate
import java.time.LocalDateTime
import kotlin.random.Random

/**
 * @author petrique
 */
class TestUtils {

    @Autowired
    private lateinit var transactionTemplate: TransactionTemplate
    @Autowired
    private lateinit var interactionDao: InteractionDao
    @Autowired
    private lateinit var subscriptionDao: SubscriptionDao
    @Autowired
    private lateinit var forecastDao: ForecastDao

    private val generator = Random

    fun insertNewInteraction(): Interaction {
        val interaction = Interaction(
            id = interactionDao.generateInteractionId(),
            updateId = generator.nextLong(),
            userId = generator.nextLong(),
            userName = generator.nextLong().toString(),
            chatId = generator.nextLong(),
            chatType = ChatType.PRIVATE,
            dateTime = LocalDateTime.now(),
            text = generator.nextLong().toString(),
            keyWords = emptyList(),
            processed = false
        )
        transactionTemplate.execute { interactionDao.insert(interaction) }
        return interaction
    }

    fun insertNewSubscription(): Subscription {
        val subscription = Subscription(
            id = subscriptionDao.generateSubscriptionId(),
            userId = generator.nextLong(),
            userName = generator.nextLong().toString(),
            chatId = generator.nextLong(),
            chatType = ChatType.PRIVATE
        )
        transactionTemplate.execute { subscriptionDao.insert(subscription) }
        return subscription
    }

    fun insertNewForecast(epochTime: Long, clouds: Int): Forecast {
        val forecast = Forecast(
            id = forecastDao.generateForecastId(),
            epochTime = epochTime,
            clouds = clouds
        )
        transactionTemplate.execute { forecastDao.insert(forecast) }
        return forecast
    }

    fun generateLong() = generator.nextLong()
}
