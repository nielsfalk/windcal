package de.nielsfalk.windcal

import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.testing.*
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.contains
import strikt.assertions.isEqualTo
import strikt.assertions.isNotNull

class ServerKtTest {
    @Test
    fun `http request for wind ical`() {
        withTestApplication({ module { mockClient() } }) {
            handleRequest(HttpMethod.Get, "/wind.ics?latlng=52.47301625304697,13.399166578830597&bestWindDirection=0,180").apply {
                expectThat(response.status()).isEqualTo(OK)
                expectThat(response.content).isNotNull().contains("wind 6.80\\ngust 13.22\\ndirection 18% match - 286.5\\nrainPrecipitationRate 0.0")
            }
        }
    }
}