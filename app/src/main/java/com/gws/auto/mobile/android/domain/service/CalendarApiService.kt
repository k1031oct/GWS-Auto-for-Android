package com.gws.auto.mobile.android.domain.service

import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventDateTime
import com.gws.auto.mobile.android.domain.model.Holiday
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalendarApiService @Inject constructor(
    private val authorizer: GoogleApiAuthorizer
) {

    private suspend fun getService(): Calendar {
        val credential = authorizer.getCredential(listOf(CalendarScopes.CALENDAR_READONLY)) ?: throw IllegalStateException("User not authenticated")
        return Calendar.Builder(authorizer.httpTransport, authorizer.jsonFactory, credential)
            .setApplicationName("GWS Automater")
            .build()
    }

    suspend fun createEvent(calendarId: String, title: String, startTime: DateTime, endTime: DateTime): Event = withContext(Dispatchers.IO) {
        val event = Event().apply {
            summary = title
            start = EventDateTime().setDateTime(startTime)
            end = EventDateTime().setDateTime(endTime)
        }
        getService().events().insert(calendarId, event).execute()
    }

    suspend fun getHolidays(countryCode: String, year: Int, month: Int): List<Holiday> = withContext(Dispatchers.IO) {
        val service = getService()
        val calendarId = "holiday@group.v.calendar.google.com".replace("holiday", "en.$countryCode#holiday")

        val timeMin = DateTime(Date.from(LocalDate.of(year, month, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()))
        val timeMax = DateTime(Date.from(LocalDate.of(year, month, 1).plusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()))

        try {
            val events = service.events().list(calendarId)
                .setTimeMin(timeMin)
                .setTimeMax(timeMax)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute()

            events.items.mapNotNull { event ->
                event.start.date?.value?.let { dateValue ->
                    val localDate = LocalDate.ofEpochDay(dateValue / (24 * 60 * 60 * 1000))
                    Holiday(localDate, event.summary)
                }
            }
        } catch (e: Exception) {
            // Likely the holiday calendar for the region does not exist.
            emptyList()
        }
    }
}
