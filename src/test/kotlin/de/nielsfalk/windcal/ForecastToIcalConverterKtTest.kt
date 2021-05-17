package de.nielsfalk.windcal

import net.fortuna.ical4j.model.Calendar
import org.junit.jupiter.api.Test
import strikt.api.DescribeableBuilder
import strikt.api.expectThat
import strikt.assertions.contains

class ForecastToIcalConverterKtTest {
    @Test
    fun `convert forecast to ical`() {
        val ical = aForecast().data.toIcal()

        expectThat(ical)
            .isValidIcal()
            .get { toString() }
            .contains("BEGIN:VCALENDAR")
            .contains("wind 6.8 (13.22)")
            .contains("""DESCRIPTION:wind 6.8\ngust 13.22\ndirection 286.5\nrainPrecipitationRate 0.0""")
            .contains("VERSION:2.0")
            .contains("UID:windcal+1620799200")
    }

    @Test
    fun `convert forcast to ical with direction percentage`() {
        val ical = aForecast().data.toIcal(listOf(0f, 180f))
        expectThat(ical)
            .isValidIcal()
            .get { toString() }
            .contains("BEGIN:VCALENDAR")
            .contains("SUMMARY:wind 6.8 (13.22)  18% direction match")
            .contains("""DESCRIPTION:wind 6.8\ngust 13.22\ndirection 18% match - 286.5\nrainPrecipitationRate 0.0""")
    }
}

private fun DescribeableBuilder<Calendar>.isValidIcal(): DescribeableBuilder<Calendar> =
    this.apply {
        get { validate() }
    }
