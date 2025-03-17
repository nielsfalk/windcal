package de.nielsfalk.windcal.converter

import biweekly.ICalendar
import biweekly.component.VEvent
import biweekly.property.Description
import biweekly.property.ProductId
import biweekly.property.Summary
import de.nielsfalk.windcal.domain.EventData
import java.time.LocalDate
import java.util.*

object CalendarConverter {
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
            productId = ProductId("PRODID:-//Niels Falk//windcal 0.1//EN")
            forEach { addEvent(it.toVEvent()) }
        }).go()

    private fun LocalDate.toDate() =
        Calendar.getInstance() //default timezone like biweekly.util.ICalDate.ICalDate() is expecting it
            .apply {
                set(year, monthValue - 1, dayOfMonth, 0, 0, 0)
            }
            .time
}