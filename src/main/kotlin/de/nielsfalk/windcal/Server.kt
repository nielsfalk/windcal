package de.nielsfalk.windcal

import io.ktor.application.*
import io.ktor.client.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.response.*
import io.ktor.routing.*
import org.intellij.lang.annotations.Language

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
            val url = "/wind.ics?latlng=52.47301625304697,13.399166578830597&bestWindDirection=0,180&filter=wind%3E6.9,wind%3C28"
            @Language("HTML") val page = """
                    <html>
                        <head><title>HELLO WIND!</title></head>
                        <body><h2>HELLO WIND!</h2>subscribe to <a href="$url">$url</a></body>
                    </html>""".trimIndent()
            call.respondText(page, contentType = ContentType.Text.Html)
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
