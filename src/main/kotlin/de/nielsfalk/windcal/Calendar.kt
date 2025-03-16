package de.nielsfalk.windcal

import biweekly.ICalendar
import biweekly.component.VEvent
import biweekly.property.Description
import biweekly.property.Summary
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.time.LocalDate
import java.util.*

suspend fun fetchCalendar(timezone: String) =
    coroutineScope {
        spots.map {
            async {
                forecast(
                    spot = it,
                    timezone = timezone
                )
                    .toIcalEvents(spotName = it.name)
            }
        }
            .awaitAll()
            .flatten()
            .toIcal()
    }

fun List<DayData>.toIcalEvents(
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

private fun LocalDate.toDate() =
    Calendar.getInstance() //default timezone like biweekly.util.ICalDate.ICalDate() is expecting it
        .apply {
            set(year, monthValue - 1, dayOfMonth, 0, 0, 0)
        }
        .time