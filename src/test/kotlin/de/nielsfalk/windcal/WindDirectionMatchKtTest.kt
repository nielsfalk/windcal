package de.nielsfalk.windcal

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class WindDirectionMatchKtTest {
    @Test
    fun `match not possible on empty BestWindDirectios`() {
        val bestOnTempelhoferFeld: BestWindDirections = listOf()

        expectThat(bestOnTempelhoferFeld.matchValue(0F)).isEqualTo(null)
    }

    @Test
    fun `detect wind direction match on tempelhofer feld`() {
        val bestOnTempelhoferFeld: BestWindDirections = listOf(0f, 180f)

        expectThat(bestOnTempelhoferFeld.matchValue(0F)).isEqualTo(100)
        expectThat(bestOnTempelhoferFeld.matchValue(45F)).isEqualTo(50)
        expectThat(bestOnTempelhoferFeld.matchValue(90F)).isEqualTo(0)
        expectThat(bestOnTempelhoferFeld.matchValue(180F)).isEqualTo(100)
        expectThat(bestOnTempelhoferFeld.matchValue(270F)).isEqualTo(0)
        expectThat(bestOnTempelhoferFeld.matchValue(359F)).isEqualTo(99)
    }

    @Test
    fun `detect wind direction match for single optimum`() {
        val bestOnTempelhoferFeld: BestWindDirections = listOf(90f)

        expectThat(bestOnTempelhoferFeld.matchValue(0F)).isEqualTo(50)
        expectThat(bestOnTempelhoferFeld.matchValue(45F)).isEqualTo(75)
        expectThat(bestOnTempelhoferFeld.matchValue(90F)).isEqualTo(100)
        expectThat(bestOnTempelhoferFeld.matchValue(180F)).isEqualTo(50)
        expectThat(bestOnTempelhoferFeld.matchValue(270F)).isEqualTo(0)
        expectThat(bestOnTempelhoferFeld.matchValue(359F)).isEqualTo(49)
    }
}