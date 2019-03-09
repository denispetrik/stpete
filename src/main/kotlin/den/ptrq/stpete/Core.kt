package den.ptrq.stpete

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate

/**
 * @author petrique
 */

@Target(AnnotationTarget.CLASS)
@Retention
annotation class MockableInTests

@Configuration
class CoreConfiguration {
    @Bean
    fun coreObjectMapperCustomizer() = Jackson2ObjectMapperBuilderCustomizer { builder ->
        builder
            .serializationInclusion(JsonInclude.Include.NON_EMPTY)
    }

    @Bean
    fun restTemplate(builder: RestTemplateBuilder) = builder.build()
}

inline fun <reified T : Any> RestTemplate.get(url: String): Response<T, ResponseError> =
    get(url, object : ParameterizedTypeReference<T>() {})

inline fun <reified T : Any> RestTemplate.post(url: String, request: Any): Response<T, ResponseError> =
    post(url, request, object : ParameterizedTypeReference<T>() {})

fun <T : Any> RestTemplate.get(
    url: String,
    responseType: ParameterizedTypeReference<T>
): Response<T, ResponseError> = exchange(url, HttpMethod.GET, null, responseType).asResponse()

fun <T : Any> RestTemplate.post(
    url: String,
    request: Any,
    responseType: ParameterizedTypeReference<T>
): Response<T, ResponseError> = exchange(url, HttpMethod.POST, HttpEntity(request), responseType).asResponse()

private fun <T : Any> ResponseEntity<T>.asResponse(): Response<T, ResponseError> =
    if (statusCode == HttpStatus.OK) {
        Response.successful(body ?: throw RuntimeException("response body is empty"))
    } else {
        Response.failed(ResponseError.TECHNICAL_ERROR)
    }

sealed class Response<R : Any, E : Any> {
    class Successful<R : Any, E : Any>(val result: R) : Response<R, E>()
    class Failed<R : Any, E : Any>(val error: E) : Response<R, E>()

    companion object {
        fun <R : Any, E : Any> successful(result: R) = Successful<R, E>(result)
        fun <R : Any, E : Any> failed(error: E) = Failed<R, E>(error)
    }
}

enum class ResponseError {
    TECHNICAL_ERROR
}

fun ObjectMapper.serialize(value: Any): String = this.writeValueAsString(value)

inline fun <reified T : Any> ObjectMapper.deserialize(serialized: String): T =
    this.readValue(serialized, object : TypeReference<T>() {})
