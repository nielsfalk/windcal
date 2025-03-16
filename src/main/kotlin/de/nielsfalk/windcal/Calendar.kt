package de.nielsfalk.windcal

import biweekly.ICalendar
import biweekly.component.VEvent
import biweekly.property.Description
import biweekly.property.Summary
import de.nielsfalk.windcal.domain.EventData
import de.nielsfalk.windcal.domain.filter
import de.nielsfalk.windcal.domain.spots
import de.nielsfalk.windcal.domain.toEventData
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.time.LocalDate
import java.util.*

suspend fun fetchCalendar(timezone: String) =
    coroutineScope {
        spots.map { spot ->
            async {
                forecast(
                    spot = spot,
                    timezone = timezone
                )
                    .filter(spot.filter)
                    .map { it.toEventData(spotName = spot.name) }
            }
        }
    }
        .awaitAll()
        .flatten()
        .toIcal()


private fun EventData.toVEvent(): VEvent =
    VEvent().apply {
        setDateStart(date.toDate(), false)
        setDateEnd(date.plusDays(1).toDate(), false)
        summary = Summary(summery)
        description = Description(this@toVEvent.description)
        setOrganizer("organizer@example.com")
    }

fun List<EventData>.toIcal(): String =
    biweekly.Biweekly.write(ICalendar().apply {
        forEach { addEvent(it.toVEvent()) }
    }).go()

private fun LocalDate.toDate() =
    Calendar.getInstance() //default timezone like biweekly.util.ICalDate.ICalDate() is expecting it
        .apply {
            set(year, monthValue - 1, dayOfMonth, 0, 0, 0)
        }
        .time