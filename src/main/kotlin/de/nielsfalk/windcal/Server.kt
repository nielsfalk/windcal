package de.nielsfalk.windcal

import io.ktor.application.*
import io.ktor.client.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(httpClient: HttpClient = theRainHttpClient()) {
    routing {
        get("/wind.ics") {
            val ical = forecast(
                location = call.parameters["latlng"].toLocation() ?: tempelhoferFeld,
                httpClient = httpClient
            ).toIcal(bestWindDirections = call.parameters["bestWindDirection"].toBestWindDirections())
            call.respondText(
                ical,
                contentType = ContentType("text", "calendar")
            )
        }
        get("/") {
            call.respondText("HELLO WIND!", contentType = ContentType.Text.Plain)
        }
    }
}

private fun String?.toBestWindDirections(): BestWindDirections? =
    this?.split(",", limit = 2)
        ?.map { it.toFloatOrNull() }
        ?.filterNotNull()

private fun String?.toLocation(): Location? =
    this?.split(",", limit = 2)
        ?.map { it.toDoubleOrNull() }
        ?.filterNotNull()
        ?.let {
            if (it.size == 2) Location(latitude = it.first(), longitude = it.last()) else null
        }
