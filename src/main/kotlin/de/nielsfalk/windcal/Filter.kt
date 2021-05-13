package de.nielsfalk.windcal

fun List<ForecastedHour>.filter(filter: String?): List<ForecastedHour> =
    if (filter == null)
        this
    else filter {
        filter.split(",").all { filter -> it.matches(filter) }
    }

private fun ForecastedHour.matches(filter: String): Boolean {
    val (field, filterVal) = filter.split("<", ">", limit = 2)
    val operator = filter.removePrefix(field).removeSuffix(filterVal)
    val fieldValue = this.get(field) ?: throw IllegalArgumentException("field $field unknown in forecast")
    return when (operator) {
        "<" -> fieldValue < filterVal.toFloat()
        ">" -> fieldValue > filterVal.toFloat()
        else -> throw IllegalArgumentException("filter operator $operator is not supported")
    }
}

private fun ForecastedHour.get(field: String): Float? =
    when (field) {
        "airTemperature" -> airTemperature
        "cape" -> cape
        "dewPointTemperature" -> dewPointTemperature
        "gust" -> gust
        "highCloudCover" -> highCloudCover
        "horizontalVisibility" -> horizontalVisibility
        "lowCloudCover" -> lowCloudCover
        "mediumCloudCover" -> mediumCloudCover
        "rainPrecipitationRate" -> rainPrecipitationRate
        "relativeHumidity" -> relativeHumidity
        "snowDepth" -> snowDepth
        "surfacePressure" -> surfacePressure
        "totalCloudCover" -> totalCloudCover
        "totalPrecipitationRate" -> totalPrecipitationRate
        "windDirection" -> windDirection
        "wind" -> wind
        else -> null
    }
