package den.ptrq.stpete

import den.ptrq.stpete.client.ClientConfiguration
import den.ptrq.stpete.forecast.ForecastConfiguration
import den.ptrq.stpete.interaction.InteractionConfiguration
import den.ptrq.stpete.subscription.SubscriptionConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import
import org.springframework.scheduling.annotation.EnableScheduling

/**
 * @author petrique
 */

@EnableAutoConfiguration
@EnableScheduling
@Import(
    CoreConfiguration::class,
    DatabaseConfiguration::class,
    ClientConfiguration::class,

    PingConfiguration::class,
    FacadeConfiguration::class,
    InteractionConfiguration::class,
    SubscriptionConfiguration::class,
    ForecastConfiguration::class
)
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
