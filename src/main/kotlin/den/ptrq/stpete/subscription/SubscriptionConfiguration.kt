package den.ptrq.stpete.subscription

import org.jooq.DSLContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * @author petrique
 */
@Configuration
class SubscriptionConfiguration {
    @Bean
    fun subscriptionDao(context: DSLContext) = SubscriptionDao(context)
}
