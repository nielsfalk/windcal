package de.nielsfalk

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit.HOURS


suspend fun main() {
    val wiek = spots.first()
    val dayDataList: List<DayData> =
        queryForecast(wiek.latitude, wiek.longitude)
            .toDayDataList(wiek.filter)

    println("result = $dayDataList")
}

@OptIn(ExperimentalSerializationApi::class)
suspend fun queryForecast(latitude: Double, longitude: Double): OpenMeteoResponse =
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
            parameter("timezone", "Europe/Berlin")
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

fun OpenMeteoResponse.toDayDataList(filter: Filter): List<DayData> {
    val sunsetRise = daily.transpose()
        .mapKeys { (instant, _) -> instant.atZone(ZoneId.of("Europe/Berlin")).toLocalDate() }
        .mapValues { (_, map) -> map.toSunsetRise() }

    return hourly.transpose()
        .mapValues { it.toHourData() }
        .values
        .groupBy { it.instant.atZone(ZoneId.of("Europe/Berlin")).toLocalDate() }
        .mapNotNull { (localDate, hourToData) ->
            hourToData
                .filter {
                    sunsetRise[localDate]?.let { sunsetRise -> it.instant in sunsetRise } ?: true
                }
                .filter(filter::invoke)
                .let {
                    if (it.size >= filter.hoursOfMatchingConditions)
                        DayData(date = localDate, hoursData = it)
                    else
                        null
                }
        }
}

private operator fun Filter.invoke(hourData: HourData): Boolean =
    hourData.run {
        val windspeed10m = windspeed10m
        val winddirection10m = winddirection10m
        val windgusts10m = windgusts10m
        windspeed10m != null &&
                windgusts10m != null &&
                windspeed10m in minWindSpeed..maxWindSpeed &&
                windgusts10m < maxGustSpeedOntop + windgusts10m &&
                windDirections?.let { winddirection10m != null && winddirection10m in it } ?: true
    }

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

data class HourData(
    val temperature2m: Double?,
    val windspeed10m: Double?,
    val winddirection10m: WindDirection?,
    val relativehumidity2m: Double?,
    val rain: Double?,
    val windgusts10m: Double?,
    val instant: Instant
)

data class DayData(
    val date: LocalDate,
    val hoursData: List<HourData>
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
