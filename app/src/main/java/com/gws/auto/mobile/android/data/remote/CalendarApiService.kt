package com.gws.auto.mobile.android.data.remote

import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.Event
import com.gws.auto.mobile.android.domain.service.GoogleApiAuthorizer
import com.gws.auto.mobile.android.domain.service.Scope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A service class responsible for interacting with the Google Calendar API.
 * It relies on [GoogleApiAuthorizer] to obtain the necessary credentials.
 */
@Singleton
class CalendarApiService @Inject constructor(private val googleApiAuthorizer: GoogleApiAuthorizer) {

    /**
     * Fetches a list of calendar events for the signed-in user within a given time range.
     *
     * @param startTime The start time for the event query.
     * @param endTime The end time for the event query.
     * @return A list of [Event] objects, or null if an error occurs. Returns an empty list if not signed in.
     */
    suspend fun getEvents(
        startTime: com.google.api.client.util.DateTime,
        endTime: com.google.api.client.util.DateTime
    ): List<Event>? {
        // If the user is not signed in, we cannot fetch events.
        if (!googleApiAuthorizer.isSignedIn()) {
            Timber.w("getEvents called but user is not signed in.")
            return emptyList()
        }

        return withContext(Dispatchers.IO) {
            try {
                // Get the credential for the required scope from the central authorizer.
                val credential = googleApiAuthorizer.getCredential(
                    listOf(Scope.CalendarReadOnly.scopeUri)
                )

                if (credential == null) {
                    Timber.e("Failed to get Google credential for Calendar API.")
                    return@withContext null
                }

                // Build the Calendar service using the authorizer's transport and factory.
                val calendar = Calendar.Builder(
                    googleApiAuthorizer.httpTransport,
                    googleApiAuthorizer.jsonFactory,
                    credential
                ).setApplicationName("GWS Auto for Android").build()

                // Execute the API call to fetch events.
                calendar.events().list("primary")
                    .setTimeMin(startTime)
                    .setTimeMax(endTime)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute()
                    .items
            } catch (e: Exception) {
                Timber.e(e, "Failed to fetch calendar events.")
                null
            }
        }
    }
}
