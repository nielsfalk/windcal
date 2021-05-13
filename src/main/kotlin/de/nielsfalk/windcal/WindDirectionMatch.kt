package de.nielsfalk.windcal

import kotlin.math.absoluteValue
import kotlin.math.roundToInt


typealias BestWindDirections = List<Float>
private typealias WindDirection = Float

internal fun BestWindDirections.matchValue(actualWindDirection: WindDirection): Int? =
    when (size) {
        0 -> null
        1 -> first().deviance(actualWindDirection).toDeviationPercentage(maxDeviation = 180)
        2 ->
            if (first().deviance(last()) == 180f)
                    (first().deviance(actualWindDirection) % 180).toDeviationPercentage(maxDeviation = 90)
            else
                throw BestWindDirectionsException("only opposite Directions are supported yet")
        else -> throw BestWindDirectionsException("$size BestWindDirectios is not yet supported")

    }

private fun WindDirection.toDeviationPercentage(maxDeviation: Int): Int {
    return ((maxDeviation - this) * 100 / maxDeviation).roundToInt()
}

class BestWindDirectionsException(msg: String) : Exception(msg)

private fun WindDirection.deviance(second: Float): Float =
    listOf(
        (this - second).absoluteValue,
        (this + 360 - second).absoluteValue,
        (second + 360 - this).absoluteValue
    ).minOrNull()!!