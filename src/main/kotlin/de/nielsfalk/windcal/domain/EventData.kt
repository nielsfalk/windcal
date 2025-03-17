package de.nielsfalk.windcal.domain

import java.time.LocalDate
import java.time.ZoneId
import java.util.*

data class EventData(
    val summery: String,
    val description: String,
    val date: LocalDate
)

fun DayData.toEventData(spotName: String) =
    EventData(
        summery = listOf(
            spotName,
            formatWindSpeed(
                average { it.windspeed10m },
                average { it.windgusts10m },
                *hoursData.mapNotNull { it.winddirection10m }.toTypedArray<WindDirection>()
            ),
            formatTemperature(average { it.temperature2m }),
            formatRain(average { it.rain }),
            "⏱${hoursData.size}"
        ).joinToString(separator = " "),
        description = hoursData.joinToString(separator = "\n") {
            listOf(
                String.format("%02d:00", it.instant.atZone(ZoneId.of("Europe/Berlin")).hour),
                formatWindSpeed(it.windspeed10m, it.windgusts10m, it.winddirection10m),
                formatTemperature(it.temperature2m),
                formatRain(it.rain)
            ).joinToString(separator = " ")
        },
        date = date
    )

private fun DayData.average(function: (HourData) -> Double?) =
    hoursData.mapNotNull { function(it) }
        .average()

private fun Double.format() = String.format(Locale.US, "%.0f", this)

fun formatRain(probability: Double?): String =
    when {
        null == probability -> ""
        probability <= 10 -> "☀️"
        probability <= 30 -> "🌤${probability.format()}%"
        probability <= 50 -> "🌥️${probability.format()}%"
        probability <= 70 -> "🌦️${probability.format()}%"
        else -> "🌧️${probability.format()}%"
    }

fun formatWindSpeed(
    knots: Double?,
    windGusts: Double?,
    vararg windDirections: WindDirection?
): String =
    when {
        null == knots -> ""
        knots < 1 -> "💤"
        knots <= 5 -> "🍃"
        knots <= 10 -> "🌬️"
        knots <= 20 -> "💨"
        knots <= 30 -> "🌪️"
        knots <= 40 -> "🌊"
        else -> "🌀"
    } +
            "${knots?.format()}(${windGusts?.format()})" +
            if (windDirections.size == 1) windDirections.first()?.arrow
            else windDirections.filterNotNull()
                .flatMap { it.arrow.toList() }
                .toSet()
                .joinToString(separator = "")

fun formatTemperature(temperature: Double?): String =
    when {
        null == temperature -> ""
        temperature < -10 -> "❄️"
        temperature <= 0.0 -> "🥶"
        temperature <= 10.0 -> "🧥"
        temperature <= 20.0 -> "🌤️"
        temperature <= 30.0 -> "☀️"
        temperature <= 40.0 -> "🥵"
        else -> "🔥"
    } + "${temperature?.format()}°"
