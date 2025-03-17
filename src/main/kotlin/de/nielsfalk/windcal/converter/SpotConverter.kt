package de.nielsfalk.windcal.converter

import de.nielsfalk.windcal.domain.Filter
import de.nielsfalk.windcal.domain.Spot
import de.nielsfalk.windcal.domain.WindDirection
import de.nielsfalk.windcal.domain.spots
import io.ktor.http.*
import io.ktor.util.*

object SpotConverter {
    private val default = Filter()
    private val defaultWindDirection = default.windDirections.orEmpty().joinToString(separator = "-")

    fun Parameters.toSpots(): List<Spot> =
        toMap().toSpots()

    fun Map<String, List<String>>.toSpots(): List<Spot> {
        val spotCount = minOf(
            this["spotname"]?.size ?: 0,
            this["latitude"]?.size ?: 0,
            this["longitude"]?.size ?: 0
        )
        return if (spotCount == 0) spots
        else {
            val name = Supplier(this["spotname"])
            val latitude = Supplier(this["latitude"])
            val longitude = Supplier(this["longitude"])
            val minWindSpeed = Supplier(this["minWindSpeed"], default.minWindSpeed)
            val maxWindSpeed = Supplier(this["mayWindSpeed"], default.maxWindSpeed)
            val maxGustSpeedOntop = Supplier(this["maxGustSpeedOntop"], default.maxGustSpeedOntop)
            val hours = Supplier(this["hours"], default.hoursOfMatchingConditions)
            val windDirections = Supplier(
                this["windDirections"], defaultWindDirection
            )

            (0 until spotCount).map {
                Spot(
                    name = name.next(),
                    latitude = latitude.next().toDouble(),
                    longitude = longitude.next().toDouble(),
                    filter = Filter(
                        windDirections = windDirections.next()
                            .split('-')
                            .filter { it.isNotBlank() }
                            .map { WindDirection.valueOf(it) },
                        minWindSpeed = minWindSpeed.next().toDouble(),
                        maxWindSpeed = maxWindSpeed.next().toDouble(),
                        maxGustSpeedOntop = maxGustSpeedOntop.next().toDouble(),
                        hoursOfMatchingConditions = hours.next().toInt()
                    )
                )
            }
        }
    }

    private class Supplier(val strings: List<String>?, val default: Any = "") {
        private val iterator = strings?.iterator()
        private lateinit var lastValue: String
        fun next(): String =
            if (strings.isNullOrEmpty()) default.toString()
            else {
                if (iterator!!.hasNext()) {
                    lastValue = iterator.next()
                }
                lastValue
            }
    }
}
