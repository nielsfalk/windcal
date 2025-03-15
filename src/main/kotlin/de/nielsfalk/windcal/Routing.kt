package de.nielsfalk.windcal

import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.NoContent
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        head("/") {
            call.respond(NoContent)
        }
        get("/wind.ics") {
            val timezone = call.queryParameters["timezone"] ?: "Europe/Berlin"
            val ical = spots.map {
                async {
                    forecast(spot = it, timezone = timezone)
                        .toIcal(spotName = it.name)
                }
            }
                .awaitAll()
                .flatten()
                .toIcal()

            call.respondText(
                text = ical,
                contentType = ContentType("text", "calendar")
            )
        }
    }
}

