package den.ptrq.stpete.forecast

import den.ptrq.stpete.jooq.Sequences
import den.ptrq.stpete.jooq.Tables.FORECAST
import den.ptrq.stpete.jooq.tables.records.ForecastRecord
import org.jooq.DSLContext
import org.jooq.RecordMapper
import org.slf4j.LoggerFactory
import java.time.Instant

/**
 * @author petrique
 */
class ForecastDao(private val context: DSLContext) {

    fun generateForecastId(): Long {
        log.info("generateForecastId()")
        return context.nextval(Sequences.SEQ_FORECAST_ID).toLong()
    }

    fun insert(forecast: Forecast) {
        log.info("insert({})", forecast)
        context
            .insertInto(FORECAST)
            .set(FORECAST.ID, forecast.id)
            .set(FORECAST.EPOCH_TIME, forecast.epochTime)
            .set(FORECAST.CLOUDS, forecast.clouds)
            .execute()
    }

    fun updateClouds(forecast: Forecast) {
        log.info("updateClouds({})", forecast)
        context
            .update(FORECAST)
            .set(FORECAST.CLOUDS, forecast.clouds)
            .where(FORECAST.ID.equal(forecast.id))
            .execute()
    }

    fun selectById(id: Long): Forecast {
        log.info("selectById(id={})", id)
        return context
            .selectFrom(FORECAST)
            .where(FORECAST.ID.equal(id))
            .fetchOne(mapper)
    }

    fun selectActual(): List<Forecast> {
        log.info("selectActual()")
        return context
            .selectFrom(FORECAST)
            .where(FORECAST.EPOCH_TIME.greaterOrEqual(Instant.now().epochSecond))
            .fetch(mapper)
    }

    companion object {
        private val log = LoggerFactory.getLogger(ForecastDao::class.java)
        private val mapper = RecordMapper<ForecastRecord, Forecast> { record ->
            Forecast(
                id = record.id,
                epochTime = record.epochTime,
                clouds = record.clouds
            )
        }
    }
}
