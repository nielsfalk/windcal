package de.nielsfalk.windcal.service

import io.ktor.server.plugins.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object HtmlService {
    private var indexHTML: String? = null

    suspend fun RoutingContext.indexHtml(): String =
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
}