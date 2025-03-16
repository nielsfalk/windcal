package de.nielsfalk.windcal

import java.time.Instant
import java.time.LocalDate

data class DayData(
    val date: LocalDate,
    val hoursData: List<HourData>
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

fun Map<LocalDate, List<HourData>>.filter(
    sunsetRise: Map<LocalDate, SunsetRise>,
    filter: Filter
): List<DayData> {
    return mapNotNull { (localDate, hourToData) ->
        hourToData
            .filter {
                sunsetRise[localDate]?.let { sunsetRise -> it.instant in sunsetRise } ?: true
            }
            .filter { it in filter }
            .let {
                if (it.size >= filter.hoursOfMatchingConditions)
                    DayData(date = localDate, hoursData = it)
                else
                    null
            }
    }
}

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