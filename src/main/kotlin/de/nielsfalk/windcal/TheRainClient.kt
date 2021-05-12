package de.nielsfalk.windcal

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import kotlinx.coroutines.runBlocking
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant

private const val baseUrl = "https://api.therainery.com/"
private val tempelhoferFeld = Location(latitude = 52.47301625304697, longitude = 13.399166578830597)

fun theRainHttpClient() =
    HttpClient {
        install(JsonFeature) {
            serializer = JacksonSerializer() {
                registerModule(JavaTimeModule())
            }
        }
        defaultRequest {
            header("x-api-key", System.getProperty("theRainApiKey") ?: throw IllegalArgumentException("please provide theRainApiKey"))
        }
    }

fun main() {
    runBlocking {
        println(forecast())
    }
}

suspend fun forecast(
    location: Location = tempelhoferFeld,
    httpClient: HttpClient = theRainHttpClient()
): Forecast =
    httpClient.use { client ->
        client.get(
            urlString = "${baseUrl}forecast/weather?latitude=${location.latitude}&longitude=${location.longitude}"
        ) {
            //  parameter()
        }
    }

data class Location(val latitude: Double, val longitude: Double)

// see https://therainery.com/documentation/weather-forecast
@JsonIgnoreProperties(ignoreUnknown = true)
data class Forecast(
    val data: List<ForecastedHour>
)

data class ForecastedHour(
    val timestamp: Long,
    val date: Instant,
    val airTemperature: Float,//	Air temperature at 2m	°C
    val cape: Float,//	Convective available potential energy
    val dewPointTemperature: Float,//	The temperature of dew point, i.e. at which temperature the air reaches 100% humidity at 2m	°C
    val gust: Float,//	Wind gust Speed	m/s
    val highCloudCover: Float,//	High clouds at levels with air pressure below 450hPa. The value describes the overall cloud coverage	0-1
    val horizontalVisibility: Float,//	Horizontal visibility	m
    val lowCloudCover: Float,//	Low clouds at levels with air pressure above 800hPa. The value describes the overall cloud coverage	0-1
    val mediumCloudCover: Float,//	Medium clouds at levels with air pressure between 450hPa and 800hPa. The value describes the overall cloud coverage	0-1
    val rainPrecipitationRate: Float,//	Rain precipitation rate	kg.m-2.s-1
    val relativeHumidity: Float,//	Relative humidity at 2m	%
    val snowDepth: Float,//	Depth of snow	m
    val surfacePressure: Float,//	Surface Pressure	Pa
    val totalCloudCover: Float,//	Total cloud coverage	0-1
    val totalPrecipitationRate: Float,//	Total Precipitation rate	kg.m-2.s-1
    val windDirection: Float,//	Direction of wind at 10m above sea level	°	0° indicates wind coming from the North, 90° coming from the East, 180° coming from the South, 270° coming from the West)
    val windSpeed: Float,//	Speed of wind at 10m above sea level	m/s
)

fun Float.ms2kts() = BigDecimal(this * 1.944).setScale(2, RoundingMode.HALF_UP)