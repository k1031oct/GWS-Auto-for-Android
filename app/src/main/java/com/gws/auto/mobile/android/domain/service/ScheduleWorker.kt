package com.gws.auto.mobile.android.domain.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.gws.auto.mobile.android.R
import android.widget.Toast
import com.gws.auto.mobile.android.MainApplication
import com.gws.auto.mobile.android.data.repository.ScheduleRepository
import com.gws.auto.mobile.android.data.repository.SettingsRepository
import com.gws.auto.mobile.android.domain.engine.WorkflowEngine
import com.gws.auto.mobile.android.domain.model.Schedule
import com.gws.auto.mobile.android.domain.notification.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.Duration
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

@HiltWorker
class ScheduleWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val workflowEngine: WorkflowEngine,
    private val scheduleRepository: ScheduleRepository,
    private val settingsRepository: SettingsRepository,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val KEY_SCHEDULE_ID = "scheduleId"
    }

    override suspend fun doWork(): Result {
        val scheduleId = inputData.getString(KEY_SCHEDULE_ID) ?: return Result.failure()
        Timber.d("Running schedule: $scheduleId")

        val schedule = scheduleRepository.getScheduleById(scheduleId)
        if (schedule == null || !schedule.isEnabled) {
            Timber.w("Schedule not found or disabled: $scheduleId. Work will not be rescheduled.")
            return Result.success()
        }

        return try {
            Timber.i("Starting scheduled workflow execution: scheduleId=$scheduleId, workflowId=${schedule.workflowId}, workflowName=${schedule.workflowName}")
            val isSuccess = workflowEngine.executeWorkflow(schedule.workflowId, triggerType = "SCHEDULED")
            if (isSuccess) {
                Timber.i("Successfully executed scheduled workflow: scheduleId=$scheduleId, workflowName=${schedule.workflowName}")
                sendNotification(schedule, isSuccess = true)
                reschedule(schedule)
                Result.success()
            } else {
                Timber.e("Failed to execute scheduled workflow: scheduleId=$scheduleId, workflowName=${schedule.workflowName}")
                sendNotification(schedule, isSuccess = false)
                reschedule(schedule) // Reschedule even on failure to ensure continuity
                Result.failure()
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to execute scheduled workflow: scheduleId=$scheduleId, workflowName=${schedule.workflowName}")
            sendNotification(schedule, isSuccess = false, errorMessage = e.message)
            reschedule(schedule) // Reschedule even on failure to ensure continuity
            Result.failure()
        }
    }

    private fun reschedule(schedule: Schedule) {
        val workManager = WorkManager.getInstance(appContext)
        val nextExecutionTime = NextExecutionTimeCalculator.calculateNextExecutionTime(schedule)
        
        val now = ZonedDateTime.now()
        if (nextExecutionTime.isBefore(now) || nextExecutionTime == now) {
            Timber.w("Calculated next execution time is in the past for schedule ${schedule.id}. Check calculator logic. Forcing reschedule in 5 minutes.")
            val backupNextTime = now.plusMinutes(5)
            val delay = Duration.between(now, backupNextTime).toMillis()
            val workRequest = OneTimeWorkRequestBuilder<ScheduleWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(Data.Builder().putString(KEY_SCHEDULE_ID, schedule.id).build())
                .build()
            workManager.enqueueUniqueWork(schedule.id, ExistingWorkPolicy.REPLACE, workRequest)
            return
        }

        val delay = Duration.between(now, nextExecutionTime).toMillis()

        val workRequest = OneTimeWorkRequestBuilder<ScheduleWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(Data.Builder().putString(KEY_SCHEDULE_ID, schedule.id).build())
            .build()

        workManager.enqueueUniqueWork(
            schedule.id,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
        Timber.d("Rescheduled work for schedule ${schedule.id} to run in ${Duration.ofMillis(delay).toMinutes()} minutes.")
    }

    private suspend fun sendNotification(schedule: Schedule, isSuccess: Boolean, errorMessage: String? = null) {
        val isForeground = MainApplication.isForeground
        val notifyInForeground = settingsRepository.notifyInForeground.first()
        
        val title = if (isSuccess) "Schedule Executed" else "Schedule Failed"
        val message = if (isSuccess) {
            "Successfully executed workflow: ${schedule.workflowName}"
        } else {
            "Failed to execute workflow: ${schedule.workflowName}. Error: ${errorMessage ?: "Unknown"}"
        }

        if (!isForeground || notifyInForeground) {
            if (ContextCompat.checkSelfPermission(appContext, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                Timber.w("POST_NOTIFICATIONS permission not granted. Cannot send notification.")
                return
            }
            notificationHelper.showExecutionNotification(title, message, schedule.id.hashCode())
        }
        
        if (isForeground && !notifyInForeground) {
             withContext(Dispatchers.Main) {
                 Toast.makeText(appContext, message, Toast.LENGTH_LONG).show()
             }
        }
    }
}
