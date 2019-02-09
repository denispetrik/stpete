package den.ptrq.stpete.interaction

import com.fasterxml.jackson.databind.ObjectMapper
import den.ptrq.stpete.asLocalDateTime
import den.ptrq.stpete.asTimestamp
import den.ptrq.stpete.deserialize
import den.ptrq.stpete.jooq.Sequences.SEQ_INTERACTION_ID
import den.ptrq.stpete.jooq.Tables.INTERACTION
import den.ptrq.stpete.jooq.tables.records.InteractionRecord
import den.ptrq.stpete.serialize
import org.jooq.DSLContext
import org.slf4j.LoggerFactory
import java.util.*

/**
 * @author petrique
 */
class InteractionDao(
    private val context: DSLContext,
    private val jsonMapper: ObjectMapper
) {

    fun generateInteractionId(): Long {
        log.info("generateInteractionId()")
        return context.nextval(SEQ_INTERACTION_ID).toLong()
    }

    fun insert(interaction: Interaction) {
        log.info("insert({})", interaction)
        val serializedKeyWords = jsonMapper.serialize(interaction.keyWords)
        context
            .insertInto(INTERACTION)
            .set(INTERACTION.ID, interaction.id)
            .set(INTERACTION.UPDATE_ID, interaction.updateId)
            .set(INTERACTION.USER_ID, interaction.userId)
            .set(INTERACTION.USER_NAME, interaction.userName)
            .set(INTERACTION.CHAT_ID, interaction.chatId)
            .set(INTERACTION.CHAT_TYPE, interaction.chatType)
            .set(INTERACTION.DATE_TIME, interaction.dateTime.asTimestamp())
            .set(INTERACTION.TEXT, interaction.text)
            .set(INTERACTION.KEY_WORDS, serializedKeyWords)
            .set(INTERACTION.PROCESSED, interaction.processed)
            .execute()
    }

    fun markAsProcessed(interaction: Interaction) {
        log.info("markAsProcessed({})", interaction)
        context
            .update(INTERACTION)
            .set(INTERACTION.PROCESSED, true)
            .where(INTERACTION.ID.equal(interaction.id))
            .execute()
    }

    fun selectById(id: Long): Interaction {
        log.info("selectById(id={})", id)
        return context
            .selectFrom(INTERACTION)
            .where(INTERACTION.ID.equal(id))
            .fetchOne(this::mapper)
    }

    fun selectAll(): List<Interaction> {
        log.info("selectAll()")
        return context
            .selectFrom(INTERACTION)
            .fetch(this::mapper)
    }

    fun selectLatest(): Optional<Interaction> {
        log.info("selectLatest()")
        return context
            .selectFrom(INTERACTION)
            .orderBy(INTERACTION.UPDATE_ID.desc())
            .limit(1)
            .fetchOptional(this::mapper)
    }

    fun selectUnprocessed(limit: Int): List<Interaction> {
        log.info("selectUnprocessed(limit={})", limit)
        return context
            .selectFrom(INTERACTION)
            .where(INTERACTION.PROCESSED.isFalse)
            .orderBy(INTERACTION.DATE_TIME.asc())
            .limit(limit)
            .fetch(this::mapper)
    }

    private fun mapper(record: InteractionRecord): Interaction {
        val keyWords = jsonMapper.deserialize<List<KeyWord>>(record.keyWords)
        return Interaction(
            id = record.id,
            updateId = record.updateId,
            userId = record.userId,
            userName = record.userName,
            chatId = record.chatId,
            chatType = record.chatType,
            dateTime = record.dateTime.asLocalDateTime(),
            text = record.text,
            keyWords = keyWords,
            processed = record.processed
        )
    }

    companion object {
        private val log = LoggerFactory.getLogger(InteractionDao::class.java)
    }
}
