package de.nielsfalk

import com.openmeteo.api.Forecast
import com.openmeteo.api.Forecast.Daily.sunrise
import com.openmeteo.api.Forecast.Daily.sunset
import com.openmeteo.api.Forecast.Hourly.rain
import com.openmeteo.api.Forecast.Hourly.relativehumidity2m
import com.openmeteo.api.Forecast.Hourly.temperature2m
import com.openmeteo.api.Forecast.Hourly.winddirection10m
import com.openmeteo.api.Forecast.Hourly.windgusts10m
import com.openmeteo.api.Forecast.Hourly.windspeed10m
import com.openmeteo.api.OpenMeteo
import com.openmeteo.api.common.time.Timezone
import com.openmeteo.api.common.units.TemperatureUnit
import com.openmeteo.api.common.units.WindSpeedUnit
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit.HOURS


fun main() {
    val wiek = spots.first()
    val dayDataList: List<DayData> =
        queryForecast(wiek.latitude.toFloat(), wiek.longitude.toFloat())
            .toDayDataList(wiek.filter)

    println("result = $dayDataList")
}

fun queryForecast(latitude: Float, longitude: Float) = OpenMeteo(
    latitude = latitude,
    longitude = longitude
)
    .forecast {
        daily = Forecast.Daily {
            listOf(sunset, sunrise)
        }
        hourly = Forecast.Hourly {
            listOf(
                temperature2m,
                windspeed10m,
                winddirection10m,
                relativehumidity2m,
                rain,
                windgusts10m
            )
        }
        models = Forecast.Models {
            listOf(bestMatch)
        }
        temperatureUnit = TemperatureUnit.Celsius
        windSpeedUnit = WindSpeedUnit.KilometresPerHour // Knots have an serialization error
        forecastDays = 14
        timezone = Timezone.getTimeZone("Europe/Berlin")
    }.getOrThrow()

private fun Forecast.Response.toDayDataList(filter: Filter): List<DayData> {
    val sunsetRise = dailyValues.transpose()
        .mapKeys { (instant, _) -> instant.atZone(ZoneId.of("Europe/Berlin")).toLocalDate() }
        .mapValues { (_, map) -> map.toSunsetRise() }

    return hourlyValues.transpose()
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
        temperature2m = value[temperature2m],
        windspeed10m = value[windspeed10m]?.kmh2Kts(),
        winddirection10m = value[winddirection10m]?.let { WindDirection.fromDegrees(it.toInt()) },
        relativehumidity2m = value[relativehumidity2m]?.kmh2Kts(),
        rain = value[rain],
        windgusts10m = value[windgusts10m]?.kmh2Kts()
    )

private fun Double.kmh2Kts(): Double =
    this * kmh2ktsFactor

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
                sunrise = it[sunrise]!!,
                sunset = it[sunset]!!
            )
        }

data class SunsetRise(val sunrise: Instant, val sunset: Instant)

operator fun SunsetRise.contains(hour: Instant) =
    hour.truncatedTo(HOURS) in sunrise.truncatedTo(HOURS)..sunset.truncatedTo(HOURS)

const val kmh2ktsFactor = 0.539957f
