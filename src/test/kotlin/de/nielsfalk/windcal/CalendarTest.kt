package de.nielsfalk.windcal

import de.nielsfalk.windcal.domain.eventData
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith

class CalendarTest : FreeSpec({
    "toIcal" {
        val ical = listOf(eventData).toIcal()

        ical.lines().iterator().run {
            next() shouldBe "BEGIN:VCALENDAR"
            next() shouldBe "VERSION:2.0"
            next() shouldBe "PRODID:PRODID:-//Niels Falk//windcal 0.1//EN"
            next() shouldBe "BEGIN:VEVENT"
            next() shouldStartWith "UID:"
            next() shouldStartWith "DTSTAMP:"
            next() shouldBe "DTSTART;VALUE=DATE:20250316"
            next() shouldBe "DTEND;VALUE=DATE:20250317"
            next() shouldStartWith "SUMMARY:Wiek 💨"
            next() shouldStartWith "DESCRIPTION"
            next() shouldStartWith " "
            next() shouldStartWith " "
            next() shouldBe "ORGANIZER:mailto:organizer@example.com"
            next() shouldBe "END:VEVENT"
            next() shouldBe "END:VCALENDAR"
            next()
            hasNext() shouldBe false
        }
    }
})