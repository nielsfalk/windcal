package de.nielsfalk.windcal.converter

import de.nielsfalk.windcal.converter.SpotConverter.toSpots
import de.nielsfalk.windcal.domain.Filter
import de.nielsfalk.windcal.domain.Spot
import de.nielsfalk.windcal.domain.WindDirection.*
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class SpotConverterTest : FreeSpec({
    "parameters to spot" {
        val parameters: Map<String, List<String>> = mapOf(
            "spotname" to listOf(
                "Wiek",
                "Dranske",
                "Rosengarten",
            ),
            "latitude" to listOf(
                54.62063490149467,
                54.62387706414278,
                54.297902433870966,
                54.47065615144053,
            ),
            "longitude" to listOf(
                13.272982585116692, 13.240464823491426, 13.43044507952561
            ),
            "minWindSpeed" to listOf(11.0, 12.0, 13.0, 14.0, 11.0, 11.0, 11.0),
            "maxGustSpeedOntop" to listOf(15.0, 16.0),
            "hours" to listOf(),
            "windDirections" to listOf("SSW-SW-WSW-W-WNW-NW-NNW", "NE-ENE-E-ESE-SE-SSE-S")
        ).mapValues { (_, values) -> values.map { it.toString() } }

        parameters.toSpots() shouldBe listOf(
            Spot(
                name = "Wiek",
                latitude = 54.62063490149467,
                longitude = 13.272982585116692,
                filter = Filter(
                    windDirections = listOf(SSW, SW, WSW, W, WNW, NW, NNW),
                    minWindSpeed = 11.0,
                    maxWindSpeed = 40.0,
                    maxGustSpeedOntop = 15.0,
                    hoursOfMatchingConditions = 2
                )
            ),
            Spot(
                name = "Dranske",
                latitude = 54.62387706414278,
                longitude = 13.240464823491426,
                filter = Filter(
                    windDirections = listOf(NE, ENE, E, ESE, SE, SSE, S),
                    minWindSpeed = 12.0,
                    maxWindSpeed = 40.0,
                    maxGustSpeedOntop = 16.0,
                    hoursOfMatchingConditions = 2
                )
            ),
            Spot(
                name = "Rosengarten",
                latitude = 54.297902433870966,
                longitude = 13.43044507952561,
                filter = Filter(
                    windDirections = listOf(NE, ENE, E, ESE, SE, SSE, S),
                    minWindSpeed = 13.0,
                    maxWindSpeed = 40.0,
                    maxGustSpeedOntop = 16.0,
                    hoursOfMatchingConditions = 2
                )
            )
        )
    }
})