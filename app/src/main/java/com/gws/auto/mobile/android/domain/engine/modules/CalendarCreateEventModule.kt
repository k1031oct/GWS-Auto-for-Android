package com.gws.auto.mobile.android.domain.engine.modules

import com.google.api.client.util.DateTime
import com.gws.auto.mobile.android.domain.engine.ExecutionContext
import com.gws.auto.mobile.android.domain.model.ExecutionResult
import com.gws.auto.mobile.android.domain.engine.ModuleExecutor
import com.gws.auto.mobile.android.domain.service.CalendarApiService
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

class CalendarCreateEventModule @Inject constructor(
    private val calendarApiService: CalendarApiService
) : ModuleExecutor {
    override suspend fun execute(context: ExecutionContext): ExecutionResult {
        return try {
            val calendarId = context.resolveVariables(context.module.parameters["calendarId"] ?: "primary")
            val summary = context.resolveVariables(context.module.parameters["summary"] ?: "")
            val start = context.resolveVariables(context.module.parameters["start"] ?: "")
            val end = context.resolveVariables(context.module.parameters["end"] ?: "")
            val description = context.resolveVariables(context.module.parameters["description"] ?: "")

            if (summary.isBlank() || start.isBlank() || end.isBlank()) {
                return ExecutionResult.Error("Summary, start, and end are required.")
            }

            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val startTime = DateTime(dateFormat.parse(start))
            val endTime = DateTime(dateFormat.parse(end))

            val event = calendarApiService.createEvent(calendarId, summary, startTime, endTime, description)

            ExecutionResult.Success("Event created: ${event.htmlLink}", mapOf("eventId" to event.id))
        } catch (e: com.google.android.gms.auth.UserRecoverableAuthException) {
            throw e
        } catch (e: Exception) {
            Timber.e(e, "Failed to create calendar event")
            ExecutionResult.Error("Failed to create event: ${e.message}")
        }
    }
}
