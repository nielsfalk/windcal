package de.nielsfalk.windcal

import de.nielsfalk.windcal.WindDirection.*
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import org.intellij.lang.annotations.Language
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class DayDataTest : FreeSpec({
    "filter" - {
        val filter = Filter(
            windDirections = listOf(S, W),
            minWindSpeed = 12.0,
            maxWindSpeed = 40.0,
            maxGustSpeedOntop = 15.0,
            hoursOfMatchingConditions = 2
        )

        val validHourData = HourData(
            temperature2m = 13.0,
            windspeed10m = 20.0,
            winddirection10m = S,
            relativehumidity2m = 10.0,
            rain = 10.0,
            windgusts10m = 22.0,
            instant = Instant.now()
        )
        "hoursOfMatchingConditions" - {
            "valid" {
                mapOf(LocalDate.now() to listOf(validHourData, validHourData))
                    .filter(mapOf(), filter) shouldHaveSize 1
            }
            "invalid" {
                mapOf(LocalDate.now() to listOf(validHourData))
                    .filter(mapOf(), filter) shouldHaveSize 0

                mapOf(
                    LocalDate.now() to listOf(
                        validHourData,
                        validHourData.copy(windspeed10m = 5.0)
                    )
                )
                    .filter(mapOf(), filter) shouldHaveSize 0
            }
        }
        "Filter.contains" - {
            "valid in filter" {
                (validHourData in filter) shouldBe true
            }

            listOf(
                validHourData.copy(windspeed10m = 11.0),
                validHourData.copy(windspeed10m = 41.0),
                validHourData.copy(windgusts10m = validHourData.windspeed10m!! + filter.maxGustSpeedOntop + 1),
                validHourData.copy(winddirection10m = SSE)
            ).forEach {
                "invalid $it" {
                    (it in filter) shouldBe false
                }
            }
        }
        "SunsetRise.contains" - {
            val sunsetRise = SunsetRise(
                sunrise = LocalDate.now().atTime(8, 23, 44).atZone(ZoneId.of("Europe/Berlin"))
                    .toInstant(),
                sunset = LocalDate.now().atTime(18, 13, 44).atZone(ZoneId.of("Europe/Berlin"))
                    .toInstant()
            )
            (8..18).forEach {

                "$it in sunsetRise" {
                    val hour =
                        LocalDate.now().atTime(it, 33, 44).atZone(ZoneId.of("Europe/Berlin"))
                            .toInstant()

                    (hour in sunsetRise) shouldBe true
                }
            }
            ((0..7) + (19..23)).forEach {
                "$it not in sunsetRise" {
                    val hour =
                        LocalDate.now().atTime(it, 33, 44).atZone(ZoneId.of("Europe/Berlin"))
                            .toInstant()

                    (hour in sunsetRise) shouldBe false
                }
            }
        }
    }
})