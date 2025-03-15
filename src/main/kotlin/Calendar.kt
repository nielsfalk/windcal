package de.nielsfalk

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

private fun DayData.summery(spotName: String): String = spotName

fun DayData.description(): String = hoursData.joinToString(separator = "\n") { it.description() }

private fun HourData.description(): String =
    "${instant.atZone(ZoneId.of("Europe/Berlin")).hour}💨$windspeed10m($windgusts10m)${winddirection10m?.arrow}🌡${temperature2m}☔️$rain"

private fun LocalDate.toDate(timezone: String): Date? =
    Date.from(atStartOfDay().atZone(ZoneId.of(timezone)).toInstant())
