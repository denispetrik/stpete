package den.ptrq.stpete.common

/**
 * @author petrique
 */

enum class ChatType(val code: String) {
    PRIVATE("private"),
    GROUP("group"),
    SUPER_GROUP("supergroup"),
    CHANNEL("channel");

    companion object {
        fun byCode(code: String): ChatType = values().first { it.code == code }
    }
}
