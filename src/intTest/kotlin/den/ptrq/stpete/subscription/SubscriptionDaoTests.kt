package den.ptrq.stpete.subscription

import den.ptrq.stpete.common.ChatType.PRIVATE
import den.ptrq.stpete.test.IntTests
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.support.TransactionTemplate

/**
 * @author petrique
 */
class SubscriptionDaoTests : IntTests() {

    @Autowired
    lateinit var transactionTemplate: TransactionTemplate
    @Autowired
    lateinit var subscriptionDao: SubscriptionDao

    @Test
    fun `should save subscription`() {
        val subscription = Subscription(
            id = subscriptionDao.generateSubscriptionId(),
            userId = 1,
            userName = "userName",
            chatId = 2,
            chatType = PRIVATE
        )

        transactionTemplate.execute {
            subscriptionDao.insert(subscription)
        }

        val inserted = subscriptionDao.selectById(subscription.id)

        assertThat(inserted.id).isEqualTo(subscription.id)
        assertThat(inserted.userId).isEqualTo(subscription.userId)
        assertThat(inserted.userName).isEqualTo(subscription.userName)
        assertThat(inserted.chatId).isEqualTo(subscription.chatId)
        assertThat(inserted.chatType).isEqualTo(subscription.chatType)
    }
}
