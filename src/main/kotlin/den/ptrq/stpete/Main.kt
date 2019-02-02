package den.ptrq.stpete

import den.ptrq.stpete.client.ClientConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

/**
 * @author petrique
 */

@EnableAutoConfiguration
@Import(
    CoreConfiguration::class,
    DatabaseConfiguration::class,
//    ScheduleConfiguration::class,
    ClientConfiguration::class,

    PingConfiguration::class,
    FacadeConfiguration::class
)
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}