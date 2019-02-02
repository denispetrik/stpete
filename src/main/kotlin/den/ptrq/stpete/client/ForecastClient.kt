package den.ptrq.stpete.client

import com.fasterxml.jackson.annotation.JsonProperty
import den.ptrq.stpete.get
import org.springframework.web.client.RestTemplate

/**
 * @author petrique
 */

class ForecastClient(private val restTemplate: RestTemplate) {

    private val token = "7f9e8297d7af64322891ad76a54494f9"
    private val cityId = 498817
    private val url = "https://api.openweathermap.org/data/2.5/forecast?APPID=$token&id=$cityId&units=metric"

    fun getForecast(): ForecastResponse {
        return restTemplate
            .get<ForecastResponse>(url)
            .getValueOrElse { throw RuntimeException("forecast call failed with error ${getError()}") }
    }
}

class ForecastResponse(
    @JsonProperty("cod") val code: String,
    @JsonProperty("list") val forecastItems: List<ForecastItem>
)

class ForecastItem(
    @JsonProperty("dt") val date: Long,
    @JsonProperty("clouds") val clouds: Clouds
)

class Clouds(@JsonProperty("all") val percentage: Int)
