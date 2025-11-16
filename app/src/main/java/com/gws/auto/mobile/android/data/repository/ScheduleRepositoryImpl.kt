package com.gws.auto.mobile.android.data.repository

import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.services.calendar.Calendar
import com.gws.auto.mobile.android.domain.model.Holiday
import com.gws.auto.mobile.android.domain.model.Schedule
import com.gws.auto.mobile.android.domain.service.GoogleApiAuthorizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class ScheduleRepositoryImpl @Inject constructor(
    private val apiAuthorizer: GoogleApiAuthorizer
) : ScheduleRepository {

    override fun getSchedulesFlow(): Flow<List<Schedule>> {
        // Return an empty list to avoid any compilation issues for now.
        return flowOf(emptyList())
    }

    override suspend fun addSchedule(schedule: Schedule) {
        // TODO: Implement
    }

    override suspend fun updateSchedule(schedule: Schedule) {
        // TODO: Implement
    }

    override suspend fun getHolidays(country: String, year: Int, month: Int): List<Holiday> = withContext(Dispatchers.IO) {
        try {
            val credential = apiAuthorizer.getCredential(scopes = listOf("https://www.googleapis.com/auth/calendar.readonly"))
            if (credential == null) {
                Timber.w("User not authenticated, cannot fetch holidays.")
                return@withContext emptyList()
            }

            val calendarService = Calendar.Builder(apiAuthorizer.httpTransport, apiAuthorizer.jsonFactory, credential)
                .setApplicationName("GWS-Auto for Android")
                .build()

            // Calendar ID for public holidays in a specific country (e.g., en.usa#holiday@group.v.calendar.google.com)
            val calendarId = "en.${country.lowercase()}#holiday@group.v.calendar.google.com"
            
            val timeMin = "${year}-${String.format("%02d", month)}-01T00:00:00-00:00"
            val timeMax = "${year}-${String.format("%02d", month)}-${LocalDate.of(year, month, 1).lengthOfMonth()}T23:59:59-00:00"

            val events = calendarService.events().list(calendarId)
                .setSingleEvents(true)
                .setOrderBy("startTime")
                .execute()

            val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

            return@withContext events.items.mapNotNull { event ->
                val dateString = event.start.date?.toStringRfc3339()?.substring(0, 10)
                if (dateString != null) {
                    Holiday(LocalDate.parse(dateString, dateFormatter), event.summary)
                } else {
                    null
                }
            }
        } catch (e: GoogleJsonResponseException) {
            Timber.e(e, "Error fetching holidays from Google Calendar API. It's possible the calendar ID is incorrect or the user has no access.")
            return@withContext emptyList() // Return empty list if calendar is not found or other API error occurs
        } catch (e: Exception) {
            Timber.e(e, "Failed to fetch holidays for $country, $year-$month")
            return@withContext emptyList()
        }
    }
}
