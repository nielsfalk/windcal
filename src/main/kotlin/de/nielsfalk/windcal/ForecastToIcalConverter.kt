package de.nielsfalk.windcal

import net.fortuna.ical4j.model.Calendar
import net.fortuna.ical4j.model.DateTime
import net.fortuna.ical4j.model.component.CalendarComponent
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.property.CalScale
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
        this.summery(bestWindDirections)
    )

private fun ForecastedHour.summery(bestWindDirections: BestWindDirections?): String = """
    wind ${windSpeed.ms2kts()}
    gust ${gust.ms2kts()}
    direction${bestWindDirections?.let { " ${it.matchValue(windDirection)}% match -" }?:""} ${windDirection}
    rainPrecipitationRate ${rainPrecipitationRate}
    """.trimIndent()

private fun Instant.toIcal() = DateTime(Date.from(this))
