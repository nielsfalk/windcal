package de.nielsfalk.windcal.server

import de.nielsfalk.windcal.fetchCalendar
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.NoContent
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        head("/") {
            call.respond(NoContent)
        }
        get("/wind.ics") {
            call.respondText(
                text = fetchCalendar(call.queryParameters["timezone"] ?: "Europe/Berlin"),
                contentType = ContentType("text", "calendar")
            )
        }
    }
}
