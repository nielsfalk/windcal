package de.nielsfalk.windcal

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.contains
import strikt.assertions.isEqualTo

class ForecastToIcalConverterKtTest{
    @Test
    fun `convert forcast to ical`() {
        val ical = aForecast().toIcal()

        expectThat(ical).contains("BEGIN:VCALENDAR")
            .contains("""wind 6.80\ngust 13.22\ndirection 286.5\nrainPrecipitationRate 0.0""")
    }
}