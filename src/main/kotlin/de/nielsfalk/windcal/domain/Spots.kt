package de.nielsfalk.windcal.domain

import de.nielsfalk.windcal.domain.WindDirection.*

data class Spot(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val filter: Filter = Filter(),
)

val spots = listOf(
    Spot(
        name = "Wiek",
        latitude = 54.62063490149467,
        longitude = 13.272982585116692,
        filter = Filter(
            windDirections = listOf(SSW, SW, WSW, W, WNW, NW, NNW)
        )
    ),
    Spot(
        name = "Dranske",
        latitude = 54.62387706414278,
        longitude = 13.240464823491426,
        filter = Filter(
            windDirections = listOf(NE, ENE, E, ESE, SE, SSE, S)
        )
    ),
    Spot(
        name = "Rosengarten",
        latitude = 54.297902433870966,
        longitude = 13.43044507952561,
        filter = Filter(
            windDirections = listOf(NE, ENE, E, ESE, SE, SSE, S)
        )
    ),
    Spot(
        name = "Suhrendorf",
        latitude = 54.47065615144053,
        longitude = 13.133100187058874,
        filter = Filter(
            windDirections = listOf(N, NNE, SW, WSW, W, WNW, NW, NNW)
        )
    ),
    Spot(
        name = "Fehmarn",
        latitude = 54.41481918607171,
        longitude = 11.068550513596511
    ),
    Spot(
        name = "Ringkøbing",
        latitude = 55.998947365672606,
        longitude = 8.213174820926131
    ),
    Spot(
        name = "Büsum",
        latitude = 54.1321307514114,
        longitude = 8.830689635176629,
        filter = Filter(
            windDirections = listOf(S, SSW, SW, WSW, W, WNW, NW)
        )
    )
)