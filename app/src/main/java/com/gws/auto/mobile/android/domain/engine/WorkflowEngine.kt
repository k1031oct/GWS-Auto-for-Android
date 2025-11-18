package com.gws.auto.mobile.android.domain.engine

import com.gws.auto.mobile.android.data.repository.HistoryRepository
import com.gws.auto.mobile.android.data.repository.WorkflowRepository
import com.gws.auto.mobile.android.domain.model.History
import com.gws.auto.mobile.android.domain.model.Module
import com.gws.auto.mobile.android.domain.model.ExecutionResult
import timber.log.Timber
import java.util.Date
import javax.inject.Inject

class WorkflowEngine @Inject constructor(
    private val moduleExecutorProvider: ModuleExecutorProvider,
    private val historyRepository: HistoryRepository,
    private val workflowRepository: WorkflowRepository
) {

    suspend fun executeWorkflow(workflowId: String) {
        val workflow = workflowRepository.getWorkflowById(workflowId) ?: return
        val modules = workflow.modules
        val executionStartTime = Date()
        val logBuilder = StringBuilder()
        var status = "Success"
        val variables = mutableMapOf<String, Any>()

        try {
            logBuilder.append("Workflow '${workflow.name}' started.\n")
            for ((index, module) in modules.withIndex()) {
                if (!module.isEnabled) {
                    logBuilder.append("[Step ${index + 1}: ${module.type}] - Skipped (disabled)\n")
                    continue
                }

                val context = ExecutionContext(module, variables)
                val executor = moduleExecutorProvider.get(module.type)

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
                workflowName = workflow.name,
                executedAt = executionStartTime,
                status = status,
                logs = logBuilder.toString()
            )
            historyRepository.insertHistory(history)
            Timber.d("Saved execution history for workflow: ${workflow.name}")
        }
    }
    
    suspend fun executeSingleModule(module: Module): ExecutionResult {
        val variables = mutableMapOf<String, Any>() // Empty context for single execution
        val context = ExecutionContext(module, variables)
        val executor = moduleExecutorProvider.get(module.type)

        if (executor == null) {
            val errorMsg = "No executor found for module type: ${module.type}"
            Timber.e(errorMsg)
            return ExecutionResult(isSuccess = false, outputMessage = errorMsg)
        }

        try {
            return executor.execute(context)
        } catch (e: Exception) {
            val errorMsg = "Exception during module execution: ${module.type}"
            Timber.e(e, errorMsg)
            return ExecutionResult(isSuccess = false, outputMessage = "$errorMsg - ${e.message}")
        }
    }
}
