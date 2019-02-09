package den.ptrq.stpete.interaction

import den.ptrq.stpete.jooq.Sequences.SEQ_INTERACTION_ID
import den.ptrq.stpete.jooq.Tables.INTERACTION
import den.ptrq.stpete.jooq.tables.records.InteractionRecord
import den.ptrq.stpete.toTimestamp
import org.jooq.DSLContext
import org.jooq.RecordMapper
import org.slf4j.LoggerFactory
import java.util.*

/**
 * @author petrique
 */
class InteractionDao(private val context: DSLContext) {

    fun generateInteractionId(): Long {
        log.info("generateInteractionId()")
        return context.nextval(SEQ_INTERACTION_ID).toLong()
    }

    fun insert(interaction: Interaction) {
        log.info("insert({})", interaction)
        context
            .insertInto(INTERACTION)
            .set(INTERACTION.ID, interaction.id)
            .set(INTERACTION.UPDATE_ID, interaction.updateId)
            .set(INTERACTION.USER_ID, interaction.userId)
            .set(INTERACTION.USER_NAME, interaction.userName)
            .set(INTERACTION.CHAT_ID, interaction.chatId)
            .set(INTERACTION.CHAT_TYPE, interaction.chatType)
            .set(INTERACTION.DATE_TIME, interaction.dateTime.toTimestamp())
            .set(INTERACTION.TEXT, interaction.text)
            .execute()
    }

    fun selectAllInteractions(): List<Interaction> {
        log.info("selectAllInteractions()")
        return context
            .selectFrom(INTERACTION)
            .fetch(mapper)
    }

    fun selectLatestInteraction(): Optional<Interaction> {
        log.info("selectLatestInteraction()")
        return context
            .selectFrom(INTERACTION)
            .orderBy(INTERACTION.UPDATE_ID.desc())
            .limit(1)
            .fetchOptional(mapper)
    }

    companion object {
        private val log = LoggerFactory.getLogger(InteractionDao::class.java)
        private val mapper = RecordMapper<InteractionRecord, Interaction> { record ->
            Interaction(
                id = record.id,
                updateId = record.updateId,
                userId = record.userId,
                userName = record.userName,
                chatId = record.chatId,
                chatType = record.chatType,
                dateTime = record.dateTime.toLocalDateTime(),
                text = record.text
            )
        }
    }
}
