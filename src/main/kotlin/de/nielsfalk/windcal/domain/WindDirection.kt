package de.nielsfalk.windcal.domain

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