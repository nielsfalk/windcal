package de.nielsfalk.windcal

import biweekly.ICalendar
import biweekly.component.VEvent
import biweekly.property.Description
import biweekly.property.Summary
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

fun List<DayData>.toIcal(
    spotName: String
): List<VEvent> =
    map {
        VEvent().apply {
            setDateStart(it.date.toDate(), false)
            setDateEnd(it.date.plusDays(1).toDate(), false)
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
    return "$spotName💨$windspeed10m($windgusts10m)${winddirection10m}🌡${temperature2m}°☔️$rain%⏱${hoursData.size}"
}

private fun <E> List<E>.average(function: (E) -> Double?) =
    mapNotNull { function(it) }
    .average()
    .format()

private fun Double.format() = String.format(Locale.US, "%.0f", this)

fun DayData.description(): String = hoursData.joinToString(separator = "\n") { it.description() }

private fun HourData.description(): String {
    val hour = instant.atZone(ZoneId.of("Europe/Berlin")).hour
    val windspeed10m1 = windspeed10m?.format()
    val windgusts10m1 = windgusts10m?.format()
    val winddirection10m = winddirection10m?.arrow
    val temperature2m1 = temperature2m?.format()
    val rain1 = rain?.format()
    return "$hour💨$windspeed10m1($windgusts10m1)${winddirection10m}🌡${temperature2m1}°☔️$rain1%"
}

private fun LocalDate.toDate() =
    Calendar.getInstance() //default timezone like biweekly.util.ICalDate.ICalDate() is expecting it
        .apply {
            set(year, monthValue - 1, dayOfMonth, 0, 0, 0)
        }
        .time
