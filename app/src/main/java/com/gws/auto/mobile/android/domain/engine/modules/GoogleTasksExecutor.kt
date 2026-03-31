package com.gws.auto.mobile.android.domain.engine.modules

import com.gws.auto.mobile.android.domain.engine.ExecutionContext
import com.gws.auto.mobile.android.domain.engine.ModuleExecutor
import com.gws.auto.mobile.android.domain.model.ExecutionResult
import com.gws.auto.mobile.android.domain.service.TasksApiService
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

import com.gws.auto.mobile.android.data.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime

class GoogleTasksExecutor @Inject constructor(
    private val tasksApiService: TasksApiService,
    private val settingsRepository: SettingsRepository
) : ModuleExecutor {

    override suspend fun execute(context: ExecutionContext): ExecutionResult {
        return try {
            val title = context.resolveVariables(context.module.parameters["title"] ?: "")
            val notes = context.resolveVariables(context.module.parameters["notes"] ?: "")
            val dueDateStr = context.resolveVariables(context.module.parameters["due_date"] ?: "")
            val dueTimeStr = context.resolveVariables(context.module.parameters["due_time"] ?: "")
            var taskListId = context.resolveVariables(context.module.parameters["task_list_id"] ?: "@default")
            val newTaskListName = context.resolveVariables(context.module.parameters["new_task_list_name"] ?: "")

            if (title.isBlank()) {
                return ExecutionResult.Error("Task title is required")
            }

            // Handle Task List creation
            if (taskListId == "create_new") {
                if (newTaskListName.isBlank()) {
                    return ExecutionResult.Error("New task list name is required")
                }
                val newTaskList = tasksApiService.createTaskList(newTaskListName)
                taskListId = newTaskList.id
            }

            // Handle Due Date and Time
            var finalNotes = notes
            val dueDate: com.google.api.client.util.DateTime? = if (dueDateStr.isNotBlank()) {
                try {
                    val date = LocalDate.parse(dueDateStr, DateTimeFormatter.ISO_LOCAL_DATE)
                    val time = if (dueTimeStr.isNotBlank()) {
                        LocalTime.parse(dueTimeStr, DateTimeFormatter.ISO_LOCAL_TIME)
                    } else {
                        LocalTime.MIN
                    }

                    val country = settingsRepository.holidayCountry.first()
                    val zoneId = getZoneIdForCountry(country)

                    val dateTime = LocalDateTime.of(date, time)
                    val zonedDateTime = ZonedDateTime.of(dateTime, zoneId)
                    
                    // Append time to notes if time is specified
                    if (dueTimeStr.isNotBlank()) {
                        val timeFormatted = time.format(DateTimeFormatter.ofPattern("HH:mm"))
                        finalNotes = if (finalNotes.isBlank()) {
                            "Due Time: $timeFormatted"
                        } else {
                            "$finalNotes\nDue Time: $timeFormatted"
                        }
                    }

                    // Create DateTime with timezone offset
                    // We use the offset from ZonedDateTime to ensure the date is interpreted correctly in that timezone.
                    // Note: Google Tasks API discards time, but using the correct offset ensures the date doesn't shift if converted to UTC.
                    val dateWithOffset = zonedDateTime.toInstant().toEpochMilli()
                    val offsetMinutes = zoneId.rules.getOffset(zonedDateTime.toInstant()).totalSeconds / 60
                    com.google.api.client.util.DateTime(dateWithOffset, offsetMinutes.toInt())
                } catch (e: Exception) {
                    null
                }
            } else {
                null
            }

            val task = tasksApiService.createTask(
                taskListId = taskListId,
                title = title,
                notes = finalNotes,
                dueDate = dueDate
            )

            ExecutionResult.Success(
                message = "Task created: ${task.title} in list $taskListId",
                variables = mapOf("taskId" to (task.id ?: ""), "taskListId" to taskListId)
            )
        } catch (e: com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException) {
            throw e.cause ?: e
        } catch (e: com.google.android.gms.auth.UserRecoverableAuthException) {
            throw e
        } catch (e: Exception) {
            ExecutionResult.Error("Failed to create task: ${e.message}")
        }
    }

    private fun getZoneIdForCountry(countryCode: String): ZoneId {
        return try {
            when (countryCode.uppercase()) {
                "JP" -> ZoneId.of("Asia/Tokyo")
                "US" -> ZoneId.of("America/New_York") // Default to NY for US
                "UK", "GB" -> ZoneId.of("Europe/London")
                "AU" -> ZoneId.of("Australia/Sydney")
                else -> ZoneId.systemDefault()
            }
        } catch (e: Exception) {
            ZoneId.systemDefault()
        }
    }
}
