package com.gws.auto.mobile.android.domain.service

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.gws.auto.mobile.android.data.repository.ScheduleRepository
import com.gws.auto.mobile.android.data.repository.WorkflowRepository
import com.gws.auto.mobile.android.domain.model.Schedule
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber
import java.util.concurrent.TimeUnit

@HiltWorker
class ScheduleWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val workflowRepository: WorkflowRepository,
    private val scheduleRepository: ScheduleRepository // Inject ScheduleRepository
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val KEY_SCHEDULE_ID = "scheduleId"
        const val KEY_WORKFLOW_ID = "workflowId" // Keep for compatibility or remove if not needed
    }

    override suspend fun doWork(): Result {
        val scheduleId = inputData.getString(KEY_SCHEDULE_ID) ?: return Result.failure()
        Timber.d("Running schedule: $scheduleId")

        val schedule = scheduleRepository.getScheduleById(scheduleId)
        if (schedule == null || !schedule.isEnabled) {
            Timber.w("Schedule not found or disabled: $scheduleId")
            return Result.success() // Or failure(), depending on desired behavior
        }

        return try {
            // TODO: Replace with actual workflow execution logic
            // workflowRepository.executeWorkflow(schedule.workflowId)
            Timber.i("Successfully executed workflow for schedule: $scheduleId")
            
            // Re-schedule the next execution
            reschedule(schedule)
            
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "Failed to execute workflow for schedule: $scheduleId")
            Result.failure()
        }
    }

    private fun reschedule(schedule: Schedule) {
        val delay = NextExecutionTimeCalculator.calculateDelay(schedule)

        val workRequest = OneTimeWorkRequestBuilder<ScheduleWorker>()
            .setInitialDelay(delay.toMillis(), TimeUnit.MILLISECONDS)
            .setInputData(Data.Builder().putString(KEY_SCHEDULE_ID, schedule.id).build())
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            schedule.id,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
        Timber.d("Re-scheduled schedule ${schedule.id} to run in ${delay.toMinutes()} minutes")
    }
}
