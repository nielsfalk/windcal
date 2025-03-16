package de.nielsfalk.windcal

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
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit.HOURS

suspend fun forecast(spot: Spot, timezone: String = "Europe/Berlin") =
    queryForecast(spot.latitude, spot.longitude, timezone)
        .toDayDataList(spot.filter, timezone)

@OptIn(ExperimentalSerializationApi::class)
suspend fun queryForecast(
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

fun OpenMeteoResponse.toDayDataList(
    filter: Filter,
    timezone: String = "Europe/Berlin"
): List<DayData> {
    val groupBy: Map<LocalDate, List<HourData>> = hourly.transpose()
        .mapValues { it.toHourData() }
        .values
        .groupBy { it.instant.atZone(ZoneId.of(timezone)).toLocalDate() }
    return groupBy
        .filter(
            sunsetRise = getSunsetRise(timezone),
            filter = filter
        )
}

private fun OpenMeteoResponse.getSunsetRise(timezone: String) =
    daily.transpose()
        .mapKeys { (instant, _) -> instant.atZone(ZoneId.of(timezone)).toLocalDate() }
        .mapValues { (_, map) -> map.toSunsetRise() }

private fun Map<String, Array<Double?>>.transpose(
): Map<Instant, Map<String, Double>> =
    this["time"]!!.mapIndexed { index, value ->
        val keysForResult = keys - "time"
        Instant.ofEpochSecond(value!!.toLong()) to
                keysForResult.mapNotNull { key ->
                    if (index < this[key]!!.size)
                        this[key]
                            ?.let { it[index] }
                            ?.let { key to it }
                    else null
                }.toMap()
    }.toMap()

private fun Map.Entry<Instant, Map<String, Double>>.toHourData() =
    HourData(
        instant = key,
        temperature2m = value["temperature_2m"],
        windspeed10m = value["windspeed_10m"],
        winddirection10m = value["winddirection_10m"]?.let { WindDirection.fromDegrees(it.toInt()) },
        relativehumidity2m = value["relativehumidity_2m"],
        rain = value["rain"],
        windgusts10m = value["windgusts_10m"]
    )


private fun Map<String, Double>.toSunsetRise() =
    mapValues { Instant.ofEpochSecond(it.value.toLong()) }
        .let {
            SunsetRise(
                sunrise = it["sunrise"]!!,
                sunset = it["sunset"]!!
            )
        }

data class SunsetRise(val sunrise: Instant, val sunset: Instant)

operator fun SunsetRise.contains(hour: Instant) =
    hour.truncatedTo(HOURS) in sunrise.truncatedTo(HOURS)..sunset.truncatedTo(HOURS)
