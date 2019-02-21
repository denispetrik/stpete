package den.ptrq.stpete.forecast

import den.ptrq.stpete.test.IntTests
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.support.TransactionTemplate

/**
 * @author petrique
 */
class ForecastDaoTests : IntTests() {

    @Autowired
    lateinit var transactionTemplate: TransactionTemplate
    @Autowired
    lateinit var forecastDao: ForecastDao

    @Test
    fun `should save forecast`() {
        val forecast = Forecast(
            id = forecastDao.generateForecastId(),
            epochTime = 1,
            clouds = 40
        )

        transactionTemplate.execute {
            forecastDao.insert(forecast)
        }

        val inserted = forecastDao.selectById(forecast.id)

        assertThat(inserted.id).isEqualTo(forecast.id)
        assertThat(inserted.epochTime).isEqualTo(forecast.epochTime)
        assertThat(inserted.clouds).isEqualTo(forecast.clouds)
    }
}
