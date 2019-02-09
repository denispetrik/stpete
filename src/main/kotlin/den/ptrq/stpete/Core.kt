package den.ptrq.stpete

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.module.kotlin.KotlinModule
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

@Configuration
class CoreConfiguration {
    @Bean
    fun coreObjectMapperCustomizer() = Jackson2ObjectMapperBuilderCustomizer { builder ->
        builder
            .serializationInclusion(JsonInclude.Include.NON_EMPTY)
//            .modulesToInstall(KotlinModule())
    }

    @Bean
    fun restTemplate(builder: RestTemplateBuilder) = builder.build()
}

inline fun <reified T : Any> RestTemplate.get(url: String): Result<T, ResponseError> =
    get(url, object : ParameterizedTypeReference<T>() {})

inline fun <reified T : Any> RestTemplate.post(url: String, request: Any): Result<T, ResponseError> =
    post(url, request, object : ParameterizedTypeReference<T>() {})

fun <T : Any> RestTemplate.get(
    url: String,
    responseType: ParameterizedTypeReference<T>
): Result<T, ResponseError> = exchange(url, HttpMethod.GET, null, responseType).asResult()

fun <T : Any> RestTemplate.post(
    url: String,
    request: Any,
    responseType: ParameterizedTypeReference<T>
): Result<T, ResponseError> = exchange(url, HttpMethod.POST, HttpEntity(request), responseType).asResult()

private fun <T : Any> ResponseEntity<T>.asResult(): Result<T, ResponseError> =
    if (statusCode == HttpStatus.OK) {
        Result.success(body ?: throw RuntimeException("response body is empty"))
    } else {
        Result.fail(ResponseError.TECHNICAL_ERROR)
    }

enum class ResponseError {
    TECHNICAL_ERROR
}
