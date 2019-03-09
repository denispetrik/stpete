package den.ptrq.stpete.forecast

import org.assertj.core.api.Assertions.assertThat
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

    fun parametersForSplitCheck(): Stream<Arguments> {
        return Stream.of(
            arguments(emptyList<Int>(), emptyList<Pair<Int, Int>>()),
            arguments(listOf(0), listOf(0 to 3)),
            arguments(listOf(21), listOf(21 to 24)),
            arguments(listOf(6, 9), listOf(6 to 12)),
            arguments(listOf(3, 12), listOf(3 to 6, 12 to 15)),
            arguments(listOf(0, 6, 9, 15, 18, 21), listOf(0 to 3, 6 to 12, 15 to 24))
        )
    }

    @ParameterizedTest(name = "should split {0} to {1}")
    @MethodSource("parametersForSplitCheck")
    fun `should split hours array to periods`(hours: List<Int>, expectedPeriods: List<Pair<Int, Int>>) {
        val periods = splitToPeriods(hours)
        assertThat(periods).isEqualTo(expectedPeriods)
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
            16 февраля: 12-18
            17 февраля: 9-12, 18-21
            """.trimIndent()

        val message = ForecastMessageCreator().createSunnyDaysMessage(forecastList)

        assertThat(message).isEqualTo(expectedMessage)
    }
}
