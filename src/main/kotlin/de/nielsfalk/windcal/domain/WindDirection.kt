package de.nielsfalk.windcal.domain

enum class WindDirection(
    val abbreviation: String,
    val arrow: String
) {
    N("North", "↓"),
    NNE("North-Northeast", "↓↙"),
    NE("Northeast", "↙"),
    ENE("East-Northeast", "←↙"),
    E("East", "←"),
    ESE("East-Southeast", "←↖"),
    SE("Southeast", "↖"),
    SSE("South-Southeast", "↖↑"),
    S("South", "↑"),
    SSW("South-Southwest", "↗↑"),
    SW("Southwest", "↗"),
    WSW("West-Southwest", "↗→"),
    W("West", "→"),
    WNW("West-Northwest", "→↘"),
    NW("Northwest", "↘"),
    NNW("North-Northwest", "↘↓");

    companion object {
        private val degreesPerDirection = 360.0 / entries.size
        private fun Double.normalize(i: Int=360) = (this + i) % i

        fun fromDegrees(degrees: Double): WindDirection =
            entries[
                ((degrees.normalize() + degreesPerDirection / 2) / degreesPerDirection)
                    .toInt()
                        % 16
            ]
    }
}