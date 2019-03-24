package den.ptrq.stpete.forecast

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

/**
 * @author petrique
 */
@TestInstance(Lifecycle.PER_CLASS)
class ForecastMessageCreatorTests {

    @ParameterizedTest(name = "should split {0} to {1}")
    @MethodSource("parametersForSplitCheck")
    fun `should split hours to periods`(hours: List<Int>, expectedPeriods: Set<Period>) {
        val periods = splitToPeriods(hours)
        assertThat(periods).isEqualTo(expectedPeriods)
    }

    fun parametersForSplitCheck(): Stream<Arguments> {
        return Stream.of(
            arguments(emptyList<Int>(), emptySet<Period>()),
            arguments(listOf(6), setOf(Period.MORNING)),
            arguments(listOf(7, 9), setOf(Period.MORNING)),
            arguments(listOf(11), setOf(Period.MORNING)),
            arguments(listOf(12), setOf(Period.DAYTIME)),
            arguments(listOf(13, 15), setOf(Period.DAYTIME)),
            arguments(listOf(17), setOf(Period.DAYTIME)),
            arguments(listOf(18), setOf(Period.EVENING)),
            arguments(listOf(19, 21), setOf(Period.EVENING)),
            arguments(listOf(22), setOf(Period.EVENING)),
            arguments(listOf(9, 12), setOf(Period.MORNING, Period.DAYTIME)),
            arguments(listOf(10, 19), setOf(Period.MORNING, Period.EVENING)),
            arguments(listOf(7, 15, 19), setOf(Period.MORNING, Period.DAYTIME, Period.EVENING))
        )
    }

    @Test
    fun `should throw exception in case of unsupported hour`() {
        assertThatCode { splitToPeriods(listOf(2)) }.hasMessage("unsupported hour")
    }

    @Test
    fun `should create 'sunny days' message`() {
        val forecastList = listOf(
            Forecast(id = 1, epochTime = 1550307600, clouds = 0),
            Forecast(id = 2, epochTime = 1550318400, clouds = 0),
            Forecast(id = 3, epochTime = 1550383200, clouds = 0),
            Forecast(id = 4, epochTime = 1550415600, clouds = 0)
        )

        val expectedMessage = """
            16 февраля: день
            17 февраля: утро, вечер
            """.trimIndent()

        val message = ForecastMessageCreator().createSunnyDaysMessage(forecastList)

        assertThat(message).isEqualTo(expectedMessage)
    }
}
