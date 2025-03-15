package de.nielsfalk

import biweekly.ICalendar
import biweekly.component.VEvent
import biweekly.property.Description
import biweekly.property.Summary
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Date

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        get ("/wind.ics"){
            val ical = biweekly.Biweekly.write(ICalendar().apply {
                addEvent(VEvent().apply {
                    // Set start and end dates
                    val startDateTime = LocalDateTime.of(2025, 3, 15, 10, 0)
                    setDateStart(Date.from(startDateTime.toInstant(ZoneOffset.UTC)), true)
                    val endDateTime = startDateTime.plusHours(1)
                    setDateEnd(Date.from(endDateTime.toInstant(ZoneOffset.UTC)), true)

                    summary = Summary("Sample Event")
                    description = Description("This is a sample event description.")
                    setOrganizer("organizer@example.com")
                })
            }).go()

            call.respondText(
                ical,
                contentType = ContentType("text", "calendar")
            )
        }
    }
}
