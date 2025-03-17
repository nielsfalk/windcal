package de.nielsfalk.windcal.service

import de.nielsfalk.windcal.client.forecast
import de.nielsfalk.windcal.converter.CalendarConverter.toIcal
import de.nielsfalk.windcal.domain.filter
import de.nielsfalk.windcal.domain.spots
import de.nielsfalk.windcal.domain.toEventData
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

object CalendarService {
    suspend fun fetchCalendar(timezone: String) =
        coroutineScope {
            spots.map { spot ->
                async {
                    forecast(
                        spot = spot,
                        timezone = timezone
                    )
                        .filter(spot.filter)
                        .map { it.toEventData(spotName = spot.name) }
                }
            }
        }
            .awaitAll()
            .flatten()
            .toIcal()
}
