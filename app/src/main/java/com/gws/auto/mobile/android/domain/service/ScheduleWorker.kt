package com.gws.auto.mobile.android.domain.service

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gws.auto.mobile.android.data.repository.WorkflowRepository
import com.gws.auto.mobile.android.domain.engine.WorkflowEngine
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ScheduleWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val workflowRepository: WorkflowRepository,
    private val workflowEngine: WorkflowEngine
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val workflowId = inputData.getString(KEY_WORKFLOW_ID) ?: return Result.failure()

        val workflow = workflowRepository.getWorkflowById(workflowId)

        return if (workflow != null) {
            try {
                workflowEngine.execute(workflow.id, workflow.name, workflow.modules)
                Result.success()
            } catch (e: Exception) {
                Result.failure()
            }
        } else {
            Result.failure()
        }
    }

    companion object {
        const val KEY_WORKFLOW_ID = "KEY_WORKFLOW_ID"
    }
}
