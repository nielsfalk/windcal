package de.nielsfalk.windcal

import de.nielsfalk.windcal.WindDirection.*

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

enum class WindDirection(
    val abbreviation: String,
    val arrow: String,
    vararg val degrees: IntRange
) {
    N("North", "↓", 348..360, 0..11),
    NNE("North-Northeast", "↓↙", 12..33),
    NE("Northeast", "↙", 34..56),
    ENE("East-Northeast", "←↙", 57..78),
    E("East", "←", 79..101),
    ESE("East-Southeast", "←↖", 102..123),
    SE("Southeast", "↖", 124..146),
    SSE("South-Southeast", "↖↑", 147..168),
    S("South", "↑", 169..191),
    SSW("South-Southwest", "↗↑", 192..213),
    SW("Southwest", "↗", 214..236),
    WSW("West-Southwest", "↗→", 237..258),
    W("West", "→", 259..281),
    WNW("West-Northwest", "→↘", 282..303),
    NW("Northwest", "↘", 304..326),
    NNW("North-Northwest", "↘↓", 327..347);

    companion object {
        fun fromDegrees(degrees: Int) =
            entries.find { it.degrees.any { degrees in it } } ?: N
    }
}

data class Filter(
    val windDirections: List<WindDirection>? = null,
    val minWindSpeed: Double = 11.0,
    val maxWindSpeed: Double = 40.0,
    val maxGustSpeedOntop: Double = 15.0,
    val hoursOfMatchingConditions: Int = 2
)
