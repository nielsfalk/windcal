package de.nielsfalk

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.testing.*

class ApplicationTest : FreeSpec({
    "wind.ics" {
        testApplication {
            application {
                module()
            }
            client.get("/wind.ics").apply {
                status shouldBe OK
                body<String>() shouldStartWith "BEGIN:VCALENDAR"
            }
        }
    }
})
