package de.nielsfalk.windcal.server

import de.nielsfalk.windcal.fetchCalendar
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.NoContent
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText(
                text = readIndexHtml(),
                contentType = ContentType.Text.Html
            )
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

var indexHTML: String? = null
suspend fun RoutingContext.readIndexHtml(): String =
    indexHTML ?: withContext(Dispatchers.IO) {
        object {}.javaClass.classLoader.getResource("index.html")!!
            .readText()
            .replace(
                oldValue = "!!baseurl!!",
                newValue = call.request.origin.run {
                    val port = serverPort.takeIf { it != 80 }
                    "https://$serverHost${port?.let { ":$it" } ?: ""}/wind.ics"
                }
            )
            .also { indexHTML = it }
    }
