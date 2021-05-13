package de.nielsfalk.windcal

import io.ktor.application.*
import io.ktor.client.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.response.*
import io.ktor.routing.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(httpClientBuilder: () -> HttpClient = { theRainHttpClient() }) {
    install(CallLogging)
    routing {
        get("/wind.ics") {
            try {
                val ical = forecast(
                    location = call.parameters["latlng"].toLocation() ?: tempelhoferFeld,
                    httpClientBuilder = httpClientBuilder
                ).data
                    .filter(call.parameters["filter"])
                    .toIcal(bestWindDirections = call.parameters["bestWindDirection"].toBestWindDirections())
                call.respondText(
                    ical,
                    contentType = ContentType("text", "calendar")
                )
            } catch (e: Exception) {
                call.respondText(e.message ?: e.toString(), contentType = ContentType.Text.Plain, status = InternalServerError)
            }
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
