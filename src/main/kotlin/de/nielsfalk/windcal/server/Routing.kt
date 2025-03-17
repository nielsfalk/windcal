package de.nielsfalk.windcal.server

import de.nielsfalk.windcal.converter.SpotConverter
import de.nielsfalk.windcal.converter.SpotConverter.toSpots
import de.nielsfalk.windcal.service.CalendarService.fetchCalendar
import de.nielsfalk.windcal.service.HtmlService.indexHtml
import io.ktor.http.*
import io.ktor.http.ContentType.*
import io.ktor.http.HttpStatusCode.Companion.NoContent
import io.ktor.server.application.Application
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText(
                text = indexHtml(),
                contentType = Text.Html
            )
        }
        head("/") {
            call.respond(NoContent)
        }
        get("/wind.ics") {
            call.respondText(
                text = fetchCalendar(
                    spots = call.queryParameters.toSpots(),
                    timezone = call.queryParameters["timezone"] ?: "Europe/Berlin"
                ),
                contentType = ContentType("text", "calendar")
            )
        }
    }
}
