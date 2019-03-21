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

inline fun <reified T : Any> RestTemplate.get(url: String): T =
    get(url, object : ParameterizedTypeReference<T>() {})

inline fun <reified T : Any> RestTemplate.post(url: String, request: Any): T =
    post(url, request, object : ParameterizedTypeReference<T>() {})

fun <T : Any> RestTemplate.get(
    url: String,
    responseType: ParameterizedTypeReference<T>
): T = exchange(url, HttpMethod.GET, null, responseType).asResult()

fun <T : Any> RestTemplate.post(
    url: String,
    request: Any,
    responseType: ParameterizedTypeReference<T>
): T = exchange(url, HttpMethod.POST, HttpEntity(request), responseType).asResult()

private fun <T : Any> ResponseEntity<T>.asResult(): T =
    if (statusCode == HttpStatus.OK)
        body ?: throw RuntimeException("response body is empty")
    else
        throw RuntimeException("call failed with statusCode=$statusCode")

fun ObjectMapper.serialize(value: Any): String = this.writeValueAsString(value)

inline fun <reified T : Any> ObjectMapper.deserialize(serialized: String): T =
    this.readValue(serialized, object : TypeReference<T>() {})
