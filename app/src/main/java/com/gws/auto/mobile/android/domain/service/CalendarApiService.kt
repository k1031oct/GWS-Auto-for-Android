package com.gws.auto.mobile.android.domain.service

import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalendarApiService @Inject constructor(
    private val authorizer: GoogleApiAuthorizer
) {

    private fun getService(): Calendar {
        val credential = authorizer.getCredential(listOf(CalendarScopes.CALENDAR)) ?: throw IllegalStateException("User not authenticated")
        return Calendar.Builder(authorizer.httpTransport, authorizer.jsonFactory, credential)
            .setApplicationName("GWS Automater")
            .build()
    }

    suspend fun createEvent(calendarId: String, title: String, startTime: DateTime, endTime: DateTime): Event {
        val event = Event().apply {
            summary = title
            start = EventDateTime().setDateTime(startTime)
            end = EventDateTime().setDateTime(endTime)
        }
        return getService().events().insert(calendarId, event).execute()
    }
}
