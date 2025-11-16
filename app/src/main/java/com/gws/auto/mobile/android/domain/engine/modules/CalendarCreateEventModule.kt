package com.gws.auto.mobile.android.domain.engine.modules

import com.google.api.client.util.DateTime
import com.gws.auto.mobile.android.domain.engine.ExecutionContext
import com.gws.auto.mobile.android.domain.engine.ExecutionResult
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
            val title = context.resolveVariables(context.module.parameters["title"] ?: "")
            val startTimeStr = context.resolveVariables(context.module.parameters["startTime"] ?: "")
            val endTimeStr = context.resolveVariables(context.module.parameters["endTime"] ?: "")

            if (title.isBlank() || startTimeStr.isBlank() || endTimeStr.isBlank()) {
                return ExecutionResult(false, "Title, start time, and end time are required.")
            }

            val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
            val startTime = DateTime(dateFormat.parse(startTimeStr))
            val endTime = DateTime(dateFormat.parse(endTimeStr))

            val event = calendarApiService.createEvent(calendarId, title, startTime, endTime)

            ExecutionResult(true, "Successfully created event with ID: ${event.id}")
        } catch (e: Exception) {
            Timber.e(e, "Failed to create calendar event")
            ExecutionResult(false, e.message)
        }
    }
}
