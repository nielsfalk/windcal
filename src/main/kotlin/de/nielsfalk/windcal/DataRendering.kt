package de.nielsfalk.windcal

import de.nielsfalk.windcal.domain.DayData
import de.nielsfalk.windcal.domain.HourData
import de.nielsfalk.windcal.domain.WindDirection
import java.time.ZoneId
import java.util.*

fun DayData.summery(spotName: String): String =
    listOf(
        spotName,
        formatWindSpeed(
            average { it.windspeed10m },
            average { it.windgusts10m },
            *hoursData.mapNotNull { it.winddirection10m }.toTypedArray<WindDirection>()
        ),
        formatTemperature(average { it.temperature2m }),
        formatRain(average { it.rain }),
        "⏱${hoursData.size}"
    ).joinToString(separator = " ")

private fun DayData.average(function: (HourData) -> Double?) =
    hoursData.mapNotNull { function(it) }
        .average()

private fun Double.format() = String.format(Locale.US, "%.0f", this)

fun DayData.description(): String = hoursData.joinToString(separator = "\n") { it.description() }

private fun HourData.description(): String =
    listOf(
        String.format("%02d:00", instant.atZone(ZoneId.of("Europe/Berlin")).hour),
        formatWindSpeed(windspeed10m, windgusts10m, winddirection10m),
        formatTemperature(temperature2m),
        formatRain(rain)
    ).joinToString(separator = " ")

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
        temperature in -10.0..0.0 -> "🥶"
        temperature in 1.0..10.0 -> "🧥"
        temperature in 11.0..20.0 -> "🌤️"
        temperature in 21.0..30.0 -> "☀️"
        temperature in 31.0..40.0 -> "🥵"
        else -> "🔥"
    } + "${temperature?.format()}°"
