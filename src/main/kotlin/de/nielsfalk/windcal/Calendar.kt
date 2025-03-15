package de.nielsfalk.windcal

import biweekly.ICalendar
import biweekly.component.VEvent
import biweekly.property.Description
import biweekly.property.Summary
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

fun List<DayData>.toIcal(
    spotName: String,
    timezone: String
): List<VEvent> =
    map {
        VEvent().apply {
            setDateStart(it.date.toDate(timezone), false)
            setDateEnd(it.date.plusDays(1).toDate(timezone), false)
            summary = Summary(it.summery(spotName))
            description = Description(it.description())
            setOrganizer("organizer@example.com")
        }
    }

fun List<VEvent>.toIcal(): String {
    return biweekly.Biweekly.write(ICalendar().apply {
        forEach(::addEvent)
    }).go()
}

fun DayData.summery(spotName: String): String {
    val windspeed10m = hoursData.average { it.windspeed10m }
    val windgusts10m = hoursData.average { it.windgusts10m }
    val temperature2m = hoursData.average { it.temperature2m }
    val rain = hoursData.average { it.rain }
    val winddirection10m = hoursData.mapNotNull { it.winddirection10m?.arrow }
        .joinToString(separator = "")
        .toSet()
        .joinToString(separator = "")
    return "$spotName💨$windspeed10m($windgusts10m)${winddirection10m}🌡${temperature2m}☔️$rain⏱${hoursData.size}"
}

private fun <E> List<E>.average(function: (E) -> Double?) =
    mapNotNull { function(it) }
        .average()
        .let { String.format(Locale.US, "%.1f", it) }

fun DayData.description(): String = hoursData.joinToString(separator = "\n") { it.description() }

private fun HourData.description(): String =
    "${instant.atZone(ZoneId.of("Europe/Berlin")).hour}💨$windspeed10m($windgusts10m)${winddirection10m?.arrow}🌡${temperature2m}☔️$rain"

private fun LocalDate.toDate(timezone: String) =
    Calendar.getInstance() //default timezone like biweekly.util.ICalDate.ICalDate() is expecting it
        .apply {
            set(year, monthValue - 1, dayOfMonth, 0, 0, 0)
        }
        .time
