package de.nielsfalk.windcal

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.ContentType.*
import io.ktor.http.HttpMethod.Companion.Get
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.time.Instant

class TheRainClientKtTest {
    @Test
    fun `request forecast`() {
        val forecast = runBlocking { forecast{mockClient()} }

        expectThat(forecast).isEqualTo(aForecast())
    }
}

fun aForecast() =
    Forecast(
        listOf(
            ForecastedHour(
                timestamp = 1620799200,
                date = Instant.ofEpochMilli(1620799200000),
                airTemperature = 12.4f,
                cape = 3.7f,
                dewPointTemperature = 11.4f,
                gustMs = 6.8f,
                highCloudCover = 97.9f,
                horizontalVisibility = 24134.6f,
                lowCloudCover = 98.6f,
                mediumCloudCover = 5.0f,
                rainPrecipitationRate = 0.0f,
                relativeHumidity = 93.4f,
                snowDepth = 0.0f,
                surfacePressure = 100145.8f,
                totalCloudCover = 100.0f,
                totalPrecipitationRate = 0.0f,
                windDirection = 286.5f,
                windMs = 3.5f
            )
        )
    )

fun mockClient() = HttpClient(MockEngine) {
    install(JsonFeature) {
        serializer = JacksonSerializer {
            registerModule(JavaTimeModule())
        }
    }
    engine {
        addHandler { request: HttpRequestData ->
            if (request.method == Get) {
                //language=JSON
                respond(
                    """
                    {
                      "data": [
                        {
                          "timestamp": 1620799200,
                          "date": "2021-05-12T06:00:00Z",
                          "airTemperature": 12.4,
                          "cape": 3.7,
                          "dewPointTemperature": 11.4,
                          "gust": 6.8,
                          "highCloudCover": 97.9,
                          "horizontalVisibility": 24134.6,
                          "lowCloudCover": 98.6,
                          "mediumCloudCover": 5,
                          "relativeHumidity": 93.4,
                          "snowDepth": 0,
                          "surfacePressure": 100145.8,
                          "totalCloudCover": 100,
                          "totalPrecipitationRate": 0,
                          "windDirection": 286.5,
                          "windSpeed": 3.5
                        }
                      ]
                    }
                """.trimIndent(),
                    headers = headersOf(HttpHeaders.ContentType, Application.Json.toString())
                )
            } else {
                error("Unhandled ${request.method} to ${request.url}")
            }
        }
    }
}

