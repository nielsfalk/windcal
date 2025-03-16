package de.nielsfalk.windcal.domain

data class Filter(
    val windDirections: List<WindDirection>? = null,
    val minWindSpeed: Double = 11.0,
    val maxWindSpeed: Double = 40.0,
    val maxGustSpeedOntop: Double = 15.0,
    val hoursOfMatchingConditions: Int = 2
)