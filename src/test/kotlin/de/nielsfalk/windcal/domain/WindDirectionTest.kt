package de.nielsfalk.windcal.domain

import de.nielsfalk.windcal.domain.WindDirection.*
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class WindDirectionTest : FreeSpec({
    listOf(
        349 to N,
        360 to N,
        361 to N,
        0 to N,
        11 to N,
        12 to NNE,
        33 to NNE,
        34 to NE,
        56 to NE,
        57 to ENE,
        78 to ENE,
        79 to E,
        101 to E,
        102 to ESE,
        123 to ESE,
        124 to SE,
        146 to SE,
        147 to SSE,
        168 to SSE,
        169 to S,
        191 to S,
        192 to SSW,
        213 to SSW,
        214 to SW,
        236 to SW,
        237 to WSW,
        258 to WSW,
        259 to W,
        281 to W,
        282 to WNW,
        303 to WNW,
        304 to NW,
        326 to NW,
        327 to NNW,
        347 to NNW,
        -10 to N,  // Test negative values
        360+11 to N   // Test values above 360
    ).forEach { (degrees,expectedDirection) ->
        "with $degrees expect ${expectedDirection.abbreviation}" {
            WindDirection.fromDegrees(degrees.toDouble()) shouldBe expectedDirection
        }
    }
})