package de.nielsfalk.windcal.domain

import de.nielsfalk.windcal.domain.WindDirection.*
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import java.time.Instant
import java.time.LocalDate

class EventDataTest : FreeSpec({
    "description" {
        eventData.description shouldBe """
            06:00 💨12(18)→ 🧥3° ☀️
            07:00 💨14(20)→ 🧥4° ☀️
            08:00 💨15(21)→ 🧥4° ☀️
            09:00 💨16(23)→ 🧥4° ☀️
            10:00 💨17(24)→ 🧥4° ☀️
            11:00 💨16(24)→ 🧥5° ☀️
            12:00 💨14(22)→↘ 🧥5° ☀️
            13:00 💨12(21)↘ 🧥5° 🌥️50%
        """.trimIndent()
    }

    "summary" {
        eventData.summery shouldBe "Wiek 💨14(22)→↘ 🧥4° ☀️ ⏱8"
    }
})

val eventData = DayData(
    date = LocalDate.of(2025, 3, 16),
    hoursData = listOf(
        HourData(
            temperature2m = 3.4,
            windspeed10m = 12.3,
            winddirection10m = W,
            relativehumidity2m = 84.0,
            rain = 0.0,
            windgusts10m = 17.7,
            instant = Instant.parse("2025-03-16T05:00:00Z")
        ),
        HourData(
            temperature2m = 3.6,
            windspeed10m = 13.5,
            winddirection10m = W,
            relativehumidity2m = 84.0,
            rain = 0.0,
            windgusts10m = 19.6,
            instant = Instant.parse("2025-03-16T06:00:00Z")
        ),
        HourData(
            temperature2m = 4.0,
            windspeed10m = 14.7,
            winddirection10m = W,
            relativehumidity2m = 85.0,
            rain = 0.0,
            windgusts10m = 21.2,
            instant = Instant.parse("2025-03-16T07:00:00Z")
        ),
        HourData(
            temperature2m = 4.3,
            windspeed10m = 16.0,
            winddirection10m = W,
            relativehumidity2m = 86.0,
            rain = 0.0,
            windgusts10m = 23.3,
            instant = Instant.parse("2025-03-16T08:00:00Z")
        ),
        HourData(
            temperature2m = 4.4,
            windspeed10m = 16.7,
            winddirection10m = W,
            relativehumidity2m = 87.0,
            rain = 0.0,
            windgusts10m = 24.3,
            instant = Instant.parse("2025-03-16T09:00:00Z")
        ),
        HourData(
            temperature2m = 4.6,
            windspeed10m = 15.7,
            winddirection10m = W,
            relativehumidity2m = 87.0,
            rain = 0.0,
            windgusts10m = 24.1,
            instant = Instant.parse("2025-03-16T10:00:00Z")
        ),
        HourData(
            temperature2m = 4.8,
            windspeed10m = 13.8,
            winddirection10m = WNW,
            relativehumidity2m = 87.0,
            rain = 0.0,
            windgusts10m = 22.4,
            instant = Instant.parse("2025-03-16T11:00:00Z")
        ),
        HourData(
            temperature2m = 5.1,
            windspeed10m = 11.7,
            winddirection10m = NW,
            relativehumidity2m = 86.0,
            rain = 50.0,
            windgusts10m = 20.6,
            instant = Instant.parse("2025-03-16T12:00:00Z")
        )
    )
).toEventData("Wiek")
