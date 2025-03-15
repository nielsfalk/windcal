package de.nielsfalk

import io.ktor.http.*
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
        get("/wind.ics") {
            val timezone = call.queryParameters["timezone"] ?: "Europe/Berlin"
            val ical = spots.map {
                async {
                    forecast(it, timezone)
                        .toIcal(it.name, timezone)
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

