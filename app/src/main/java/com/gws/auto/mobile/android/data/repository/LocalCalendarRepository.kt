package com.gws.auto.mobile.android.data.repository

import android.content.ContentResolver
import android.content.Context
import android.provider.CalendarContract
import com.gws.auto.mobile.android.domain.model.LocalCalendarEvent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A repository for fetching calendar events from the device's local calendar provider.
 * Note: This repository requires the `android.permission.READ_CALENDAR` permission to be granted.
 */
@Singleton
class LocalCalendarRepository @Inject constructor(@ApplicationContext private val context: Context) {

    private val contentResolver: ContentResolver = context.contentResolver

    /**
     * Fetches local calendar events within a specified time range.
     *
     * @param startTimeMillis The start of the time range in epoch milliseconds.
     * @param endTimeMillis The end of the time range in epoch milliseconds.
     * @return A list of [LocalCalendarEvent]s. Returns an empty list if an error occurs or no events are found.
     */
    suspend fun getEvents(startTimeMillis: Long, endTimeMillis: Long): List<LocalCalendarEvent> {
        return withContext(Dispatchers.IO) {
            val events = mutableListOf<LocalCalendarEvent>()
            val projection = arrayOf(
                CalendarContract.Events.TITLE,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events.DTEND,
                CalendarContract.Events.ALL_DAY
            )

            // Query the calendar provider for events within the given time range.
            val selection = "(${CalendarContract.Events.DTSTART} >= ?) AND (${CalendarContract.Events.DTEND} <= ?)"
            val selectionArgs = arrayOf(startTimeMillis.toString(), endTimeMillis.toString())

            try {
                contentResolver.query(
                    CalendarContract.Events.CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    CalendarContract.Events.DTSTART + " ASC"
                )?.use { cursor ->
                    val titleIndex = cursor.getColumnIndex(CalendarContract.Events.TITLE)
                    val startIndex = cursor.getColumnIndex(CalendarContract.Events.DTSTART)
                    val endIndex = cursor.getColumnIndex(CalendarContract.Events.DTEND)
                    val allDayIndex = cursor.getColumnIndex(CalendarContract.Events.ALL_DAY)

                    while (cursor.moveToNext()) {
                        val title = cursor.getString(titleIndex)
                        val startTime = cursor.getLong(startIndex)
                        val endTime = cursor.getLong(endIndex)
                        val allDay = cursor.getInt(allDayIndex) == 1

                        events.add(LocalCalendarEvent(title, startTime, endTime, allDay))
                    }
                }
            } catch (e: SecurityException) {
                Timber.e(e, "Permission denied. READ_CALENDAR is required to fetch local events.")
                return@withContext emptyList() // Return empty on permission failure
            } catch (e: Exception) {
                Timber.e(e, "Failed to query local calendar events.")
                return@withContext emptyList() // Return empty on other errors
            }
            events
        }
    }
}
