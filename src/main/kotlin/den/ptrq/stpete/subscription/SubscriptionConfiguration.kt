package den.ptrq.stpete.subscription

import org.jooq.DSLContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @author petrique
 */
@Configuration
class SubscriptionConfiguration {

    @Bean
    fun subscriptionDao(context: DSLContext) = SubscriptionDao(context)

    @Bean
    fun subscriptionController(subscriptionDao: SubscriptionDao) = SubscriptionController(subscriptionDao)
}

@RestController
class SubscriptionController(private val subscriptionDao: SubscriptionDao) {
    @GetMapping("/subscriptions")
    fun getAll(): String {
        return subscriptionDao.selectAll().asSequence()
            .map { it.toString() }
            .joinToString(separator = "\n")
    }
}
