package de.nielsfalk.windcal

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class FilterKtTest {
    val aForecastData= aForecast().data.first()
    val inWindRange= listOf(
        aForecastData.copy(wind = 7f),
        aForecastData.copy(wind = 27.9f)
    )
    val outOfWindRange= listOf(
        aForecastData.copy(wind = 6.9f),
        aForecastData.copy(wind = 28f)
    )

    @Test
    fun `filter low wind and high wind`() {
        val filtered = (inWindRange + outOfWindRange).filter("wind>6.9,wind<28")

        expectThat(filtered).isEqualTo(inWindRange)
    }
}
