package de.nielsfalk.windcal

import net.fortuna.ical4j.model.Calendar
import net.fortuna.ical4j.model.DateTime
import net.fortuna.ical4j.model.component.CalendarComponent
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.property.CalScale
import net.fortuna.ical4j.model.property.Description
import net.fortuna.ical4j.model.property.ProdId
import java.time.Duration
import java.time.Instant
import java.util.*

fun Forecast.toIcal(bestWindDirections:BestWindDirections?=null): String =
    Calendar().apply {
        properties.add(ProdId("-//Events Calendar//iCal4j 1.0//EN"))
        properties.add(CalScale.GREGORIAN)
        components.addAll(data.map { it.toEvent(bestWindDirections) })
    }.toString()

private fun ForecastedHour.toEvent(bestWindDirections: BestWindDirections?): CalendarComponent =
    VEvent(
        date.toIcal(),
        Duration.ofHours(1),
        title(bestWindDirections)
    ).apply { properties += Description(description(bestWindDirections)) }

private fun ForecastedHour.title(bestWindDirections: BestWindDirections?): String = """
    wind ${windSpeed.ms2kts()} (${gust.ms2kts()}) ${bestWindDirections?.let { " ${it.matchValue(windDirection)}% direction match" }?:""} 
    """.trimIndent()

private fun ForecastedHour.description(bestWindDirections: BestWindDirections?): String = """
    wind ${windSpeed.ms2kts()}
    gust ${gust.ms2kts()}
    direction${bestWindDirections?.let { " ${it.matchValue(windDirection)}% match -" }?:""} ${windDirection}
    rainPrecipitationRate $rainPrecipitationRate
    airTemperature(at 2m °C) $airTemperature
    cape(Convective available potential energy) $cape
    horizontalVisibility $horizontalVisibility
    relativeHumidity(at 2m %) $relativeHumidity
    snowDepth $snowDepth
    surfacePressure(Pa) $surfacePressure
    totalCloudCover(0-1) $totalCloudCover
    totalPrecipitationRate(kg.m-2.s-1) $totalPrecipitationRate
    highCloudCover(High clouds at levels with air pressure below 450hPa. The value describes the overall cloud coverage 0-1) $highCloudCover
    mediumCloudCover(Medium clouds at levels with air pressure between 450hPa and 800hPa. The value describes the overall cloud coverage 0-1) $mediumCloudCover
    lowCloudCover(Low clouds at levels with air pressure above 800hPa. The value describes the overall cloud coverage 0-1) $lowCloudCover
    dewPointTemperature(The temperature of dew point, i.e. at which temperature the air reaches 100% humidity at 2m °C) $dewPointTemperature
    """.trimIndent()

private fun Instant.toIcal() = DateTime(Date.from(this))
