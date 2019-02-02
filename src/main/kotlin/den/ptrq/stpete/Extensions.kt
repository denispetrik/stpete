package den.ptrq.stpete

/**
 * @author petrique
 */

class Result<V : Any, E : Any>(private val value: V?, private val error: E?) {
    fun isSuccessful() = value.isNotNull()
    fun isFailed() = error.isNotNull()
    fun getValue(): V = value ?: throw RuntimeException("Value is null")
    fun getError(): E = error ?: throw RuntimeException("Error is null")
    fun getValueOrElse(elseCase: Result<V, E>.() -> V): V = if (isSuccessful()) getValue() else elseCase(this)

    companion object {
        fun <V : Any, E : Any> success(value: V) = Result<V, E>(value, null)
        fun <V : Any, E : Any> fail(error: E) = Result<V, E>(null, error)
    }
}

fun Any?.isNull() = this == null
fun Any?.isNotNull() = this != null
