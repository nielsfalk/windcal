package de.nielsfalk.windcal.service

import de.nielsfalk.windcal.domain.spots
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
                    oldValue = "/*BASE_URL*/",
                    newValue = call.request.origin.run {
                        val port = serverPort.takeIf { it != 80 }
                        "https://$serverHost${port?.let { ":$it" } ?: ""}/wind.ics"
                    }
                )
                .replace(
                    oldValue = "/*INITIAL_DATA*/",
                    newValue = spots.joinToString(separator = ",") {
                        it.run {
                            val windDirections = filter.windDirections?.joinToString(separator = "-")?:""
                            //language=JSON
                            """
                            {
                                "spotname": "$name",
                                "latitude": "$latitude",
                                "longitude": "$longitude",
                                "minWindSpeed": "${filter.minWindSpeed}",
                                "maxWindSpeed": "${filter.maxWindSpeed}",
                                "maxGustSpeedOntop": "${filter.maxGustSpeedOntop}",
                                "hours": "${filter.hoursOfMatchingConditions}",
                                "windDirections": "$windDirections"
                            }
                            """.trimIndent()
                        }

                    }
                )
                .also { indexHTML = it }
        }
}