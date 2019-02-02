package den.ptrq.stpete

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * @author petrique
 */

@Configuration
@EnableScheduling
class ScheduleConfiguration {
    @Bean
    fun updatePoller() = UpdatePoller()
}

@Component
class UpdatePoller {

    @Scheduled(fixedRate = 1000)
    fun pollUpdates() {
        log.info("current millis={}", System.currentTimeMillis())
    }

    companion object {
        val log: Logger = LoggerFactory.getLogger(UpdatePoller::class.java)
    }
}
