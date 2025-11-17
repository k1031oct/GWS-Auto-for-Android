package com.gws.auto.mobile.android.domain.service

import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventDateTime
import com.gws.auto.mobile.android.domain.model.Holiday
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
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

    private fun getHolidayCalendarId(countryCode: String): String {
        val lang = Locale.forLanguageTag(countryCode).language
        val country = when (countryCode.uppercase()) {
            "US" -> "usa"
            "JP" -> "japanese"
            // Add other supported countries here
            else -> countryCode.lowercase()
        }
        return "${lang}.${country}#holiday@group.v.calendar.google.com"
    }

    suspend fun getHolidays(countryCode: String, year: Int, month: Int): List<Holiday> = withContext(Dispatchers.IO) {
        val service = getService()
        val calendarId = getHolidayCalendarId(countryCode)

        val timeMin = DateTime(Date.from(LocalDate.of(year, month, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()))
        val timeMax = DateTime(Date.from(LocalDate.of(year, month, 1).plusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()))

        try {
            Timber.d("Fetching holidays for calendar: $calendarId")
            val events = service.events().list(calendarId)
                .setTimeMin(timeMin)
                .setTimeMax(timeMax)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute()

            events.items.mapNotNull { event ->
                val dateString = event.start?.date?.toStringRfc3339()?.substring(0, 10)
                if (dateString != null) {
                    try {
                        Holiday(LocalDate.parse(dateString, DateTimeFormatter.ISO_DATE), event.summary)
                    } catch (e: Exception) {
                        Timber.e(e, "Failed to parse date: $dateString")
                        null
                    }
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to fetch holidays for $calendarId")
            emptyList()
        }
    }
}
