package de.nielsfalk.windcal.domain

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class DayDataTest : FreeSpec({
    "filter" - {
        val filter = Filter(
            windDirections = listOf(WindDirection.S, WindDirection.W),
            minWindSpeed = 12.0,
            maxWindSpeed = 40.0,
            maxGustSpeedOntop = 15.0,
            hoursOfMatchingConditions = 2
        )

        val validHourData = HourData(
            temperature2m = 13.0,
            windspeed10m = 20.0,
            winddirection10m = WindDirection.S,
            relativehumidity2m = 10.0,
            rain = 10.0,
            windgusts10m = 22.0,
            instant = Instant.now()
        )
        "hoursOfMatchingConditions" - {
            "valid" {
                dayData(listOf(validHourData, validHourData))
                    .filter(filter) shouldHaveSize 1
            }
            "invalid" {
                dayData(listOf(validHourData)).filter(filter) shouldHaveSize 0

                dayData(
                    listOf(
                        validHourData,
                        validHourData.copy(windspeed10m = 5.0)
                    )
                ).filter(filter) shouldHaveSize 0
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
                validHourData.copy(winddirection10m = WindDirection.SSE)
            ).forEach {
                "invalid $it" {
                    (it in filter) shouldBe false
                }
            }
        }
        "SunsetRise.contains" - {
            val sunsetRise: Pair<Instant, Instant> =
                LocalDate.now().atTime(8, 23, 44)
                    .atZone(ZoneId.of("Europe/Berlin")).toInstant() to
                        LocalDate.now().atTime(18, 13, 44)
                            .atZone(ZoneId.of("Europe/Berlin"))
                            .toInstant()
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

private fun dayData(listOf: List<HourData>): List<DayData> =
    listOf(DayData(LocalDate.now(), listOf))