package de.nielsfalk.windcal.domain

import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoUnit.HOURS

data class DayData(
    val date: LocalDate,
    val hoursData: List<HourData>,
    val sunrise: Instant? = null,
    val sunset: Instant? = null
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

fun List<DayData>.filter(
    filter: Filter
): List<DayData> =
    mapNotNull { day ->
        day.copy(
            hoursData = day.hoursData
                .filter { hour ->
                    hour.instant.truncatedTo(HOURS) in day.sunrise to day.sunset
                }
                .filter { it in filter }
        )
            .takeIf { it.hoursData.size >= filter.hoursOfMatchingConditions }
    }

operator fun Pair<Instant?, Instant?>.contains(it: Instant): Boolean =
    first?.let { sunrise ->
        second?.let { sunset ->
            it.truncatedTo(HOURS) in sunrise.truncatedTo(HOURS)..sunset.truncatedTo(
                HOURS
            )
        }
    } ?: true

operator fun Filter.contains(hourData: HourData) =
    hourData.run {
        val windspeed10m = windspeed10m
        val winddirection10m = winddirection10m
        val windgusts10m = windgusts10m
        windspeed10m != null &&
                windgusts10m != null &&
                windspeed10m in minWindSpeed..maxWindSpeed &&
                windgusts10m - windspeed10m <= maxGustSpeedOntop &&
                windDirections?.let { winddirection10m != null && winddirection10m in it } ?: true
    }
