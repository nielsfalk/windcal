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

fun Forecast.toIcal(): String =
    Calendar().apply {
        properties.add(ProdId("-//Events Calendar//iCal4j 1.0//EN"))
        properties.add(CalScale.GREGORIAN)
        components.addAll(data.map { it.toEvent() })
    }.toString()

private fun ForecastedHour.toEvent(): CalendarComponent =
    VEvent(
        date.toIcal(),
        Duration.ofHours(1),
        this.summery()
    )

private fun ForecastedHour.summery(): String = """
    wind ${windSpeed.ms2kts()}
    gust ${gust.ms2kts()}
    direction ${windDirection}
    rainPrecipitationRate ${rainPrecipitationRate}
    """.trimIndent()

private fun Instant.toIcal() = DateTime(Date.from(this))
