package com.gws.auto.mobile.android.domain.engine

import com.gws.auto.mobile.android.data.repository.HistoryRepository
import com.gws.auto.mobile.android.domain.model.History
import com.gws.auto.mobile.android.domain.model.Module
import timber.log.Timber
import java.util.Date
import javax.inject.Inject

class WorkflowEngine @Inject constructor(
    private val moduleExecutors: Map<String, @JvmSuppressWildcards ModuleExecutor>,
    private val historyRepository: HistoryRepository
) {

    suspend fun execute(workflowId: String, workflowName: String, modules: List<Module>) {
        val executionStartTime = Date()
        val logBuilder = StringBuilder()
        var status = "Success"
        val variables = mutableMapOf<String, Any>()

        try {
            logBuilder.append("Workflow '$workflowName' started.\n")
            for ((index, module) in modules.withIndex()) {
                val context = ExecutionContext(module, variables)
                val executor = moduleExecutors[module.type]

                if (executor == null) {
                    val errorMsg = "No executor found for module type: ${module.type}"
                    Timber.e(errorMsg)
                    logBuilder.append("ERROR: $errorMsg\n")
                    status = "Failure"
                    break
                }

                logBuilder.append("[Step ${index + 1}: ${module.type}]\n")
                try {
                    val result = executor.execute(context)
                    logBuilder.append("Output: ${result.outputMessage ?: "No message"}\n")
                    if (!result.isSuccess) {
                        Timber.e("Module execution failed: ${result.outputMessage}")
                        status = "Failure"
                        break
                    }
                } catch (e: Exception) {
                    val errorMsg = "Exception during module execution: ${module.type}"
                    Timber.e(e, errorMsg)
                    logBuilder.append("ERROR: $errorMsg - ${e.message}\n")
                    status = "Failure"
                    break
                }
            }
        } finally {
            logBuilder.append("Workflow finished with status: $status\n")
            val history = History(
                workflowId = workflowId,
                workflowName = workflowName,
                executedAt = executionStartTime,
                status = status,
                logs = logBuilder.toString()
            )
            historyRepository.insertHistory(history)
            Timber.d("Saved execution history for workflow: $workflowName")
        }
    }
}
