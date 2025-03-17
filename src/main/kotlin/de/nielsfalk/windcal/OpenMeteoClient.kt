package de.nielsfalk.windcal

import de.nielsfalk.windcal.converter.ForecastConverter.toDayDataList
import de.nielsfalk.windcal.domain.Spot
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy

suspend fun forecast(
    spot: Spot,
    timezone: String
) = query(spot.latitude, spot.longitude, timezone)
    .toDayDataList(timezone)


@OptIn(ExperimentalSerializationApi::class)
private suspend fun query(
    latitude: Double,
    longitude: Double,
    timezone: String = "Europe/Berlin"
): OpenMeteoResponse =
    HttpClient(CIO) {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    namingStrategy = JsonNamingStrategy.SnakeCase
                }
            )
        }
    }.use {
        it.get("https://api.open-meteo.com/v1/forecast") {
            parameter("latitude", latitude.toString())
            parameter("longitude", longitude.toString())
            parameter("daily", "sunset,sunrise")
            parameter(
                "hourly",
                "temperature_2m,windspeed_10m,winddirection_10m,relativehumidity_2m,rain,windgusts_10m"
            )
            parameter("forecast_days", "14")
            parameter("timezone", timezone)
            parameter("temperature_unit", "celsius")
            parameter("windspeed_unit", "kn")
            parameter("models", "best_match")
            parameter("timeformat", "unixtime")
        }.body()
    }

@Serializable
data class OpenMeteoResponse(
    val daily: Map<String, Array<Double?>> = mapOf(),
    val hourly: Map<String, Array<Double?>> = mapOf(),
)
