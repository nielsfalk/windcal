package de.nielsfalk.windcal.converter

import de.nielsfalk.windcal.*
import de.nielsfalk.windcal.domain.DayData
import de.nielsfalk.windcal.domain.HourData
import de.nielsfalk.windcal.domain.WindDirection
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

object ForecastConverter {
    fun OpenMeteoResponse.toDayDataList(timezone: String = "Europe/Berlin"): List<DayData> =
        hourly.transpose()
            .mapValues { it.toHourData() }
            .values
            .groupBy { it.instant.atZone(ZoneId.of(timezone)).toLocalDate() }
            .toDayData(getSunsetRise(timezone))

    private fun Map.Entry<Instant, Map<String, Double>>.toHourData() =
        HourData(
            instant = key,
            temperature2m = value["temperature_2m"],
            windspeed10m = value["windspeed_10m"],
            winddirection10m = value["winddirection_10m"]?.let { WindDirection.fromDegrees(it) },
            relativehumidity2m = value["relativehumidity_2m"],
            rain = value["rain"],
            windgusts10m = value["windgusts_10m"]
        )


    private fun Map<LocalDate, List<HourData>>.toDayData(
        sunsetRise: Map<LocalDate, SunsetRise>
    ): List<DayData> =
        map { (localDate, hourToData) ->
            DayData(
                date = localDate,
                hoursData = hourToData,
                sunrise = sunsetRise[localDate]?.sunrise,
                sunset = sunsetRise[localDate]?.sunset
            )
        }

    private fun OpenMeteoResponse.getSunsetRise(timezone: String = "Europe/Berlin") =
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

    private fun Map<String, Double>.toSunsetRise() =
        mapValues { Instant.ofEpochSecond(it.value.toLong()) }
            .let {
                SunsetRise(
                    sunrise = it["sunrise"]!!,
                    sunset = it["sunset"]!!
                )
            }

    private data class SunsetRise(val sunrise: Instant, val sunset: Instant)
}