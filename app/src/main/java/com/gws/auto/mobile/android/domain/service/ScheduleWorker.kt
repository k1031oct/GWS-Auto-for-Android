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
import com.gws.auto.mobile.android.data.repository.ScheduleRepository
import com.gws.auto.mobile.android.domain.engine.WorkflowEngine
import com.gws.auto.mobile.android.domain.model.Schedule
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber
import java.time.Duration
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

@HiltWorker
class ScheduleWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val workflowEngine: WorkflowEngine,
    private val scheduleRepository: ScheduleRepository
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val KEY_SCHEDULE_ID = "scheduleId"
        private const val NOTIFICATION_CHANNEL_ID = "schedule_notifications"
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
            workflowEngine.executeWorkflow(schedule.workflowId)
            Timber.i("Successfully executed workflow for schedule: $scheduleId")
            sendNotification(schedule, isSuccess = true)
            reschedule(schedule)
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "Failed to execute workflow for schedule: $scheduleId")
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

    private fun sendNotification(schedule: Schedule, isSuccess: Boolean, errorMessage: String? = null) {
        if (ContextCompat.checkSelfPermission(appContext, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Timber.w("POST_NOTIFICATIONS permission not granted. Cannot send notification.")
            return
        }
        
        val title = if (isSuccess) "Schedule Executed" else "Schedule Failed"
        val content = if (isSuccess) {
            "Successfully executed workflow: ${schedule.workflowName}"
        } else {
            "Failed to execute workflow: ${schedule.workflowName}. Error: ${errorMessage ?: "Unknown"}"
        }

        val builder = NotificationCompat.Builder(appContext, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification) // Ensure this drawable exists
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))

        with(NotificationManagerCompat.from(appContext)) {
            // notificationId is a unique int for each notification that you must define
            notify(schedule.id.hashCode(), builder.build())
        }
    }
}
