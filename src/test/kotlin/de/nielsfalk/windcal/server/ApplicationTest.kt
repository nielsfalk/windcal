package de.nielsfalk.windcal.server

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.HttpStatusCode.Companion.NoContent
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.testing.*

class ApplicationTest : FreeSpec({
    "wind.ics" {
        withApp{
            client.get("/wind.ics").apply {
                status shouldBe OK
                body<String>() shouldStartWith "BEGIN:VCALENDAR"
            }
        }
    }
    "index.html" {
        withApp{
            client.get("/").apply {
                status shouldBe OK
                body<String>() shouldStartWith "<!"
            }
        }
    }
    "head /" {
        withApp{
            client.head("/").apply {
                status shouldBe NoContent
            }
        }
    }
})

private fun withApp(
    function: suspend ApplicationTestBuilder.() -> Unit
) {
    testApplication {
        application {
            module()
        }
        function()
    }
}
