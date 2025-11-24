package com.gws.auto.mobile.android.domain.engine

import com.gws.auto.mobile.android.data.repository.HistoryRepository
import com.gws.auto.mobile.android.data.repository.WorkflowRepository
import com.gws.auto.mobile.android.domain.model.History
import com.gws.auto.mobile.android.domain.model.Module
import com.gws.auto.mobile.android.domain.model.ExecutionResult
import timber.log.Timber
import java.util.Date
import javax.inject.Inject

/**
 * Defines the contract for a workflow execution engine.
 * This interface abstracts the mechanism of how a workflow is executed, allowing for different
 * implementations (e.g., local, remote, or test environments).
 */
interface WorkflowEngine {
    /**
     * Executes all modules within a specified workflow.
     *
     * @param workflowId The ID of the workflow to execute.
     * @param triggerType The type of trigger that initiated this execution (MANUAL or SCHEDULED).
     */
    suspend fun executeWorkflow(workflowId: String, triggerType: String = "MANUAL")

    /**
     * Executes a single, isolated module.
     * This is typically used for testing or validating a module's configuration.
     *
     * @param module The module instance to execute.
     * @return The result of the execution.
     */
    suspend fun executeSingleModule(module: Module): ExecutionResult
}

/**
 * The default, local implementation of the [WorkflowEngine].
 * It executes workflows directly on the user's device.
 */
class LocalWorkflowEngine @Inject constructor(
    private val moduleExecutorProvider: ModuleExecutorProvider,
    private val historyRepository: HistoryRepository,
    private val workflowRepository: WorkflowRepository,
    private val logMessageModule: com.gws.auto.mobile.android.domain.engine.modules.LogMessageModule
) : WorkflowEngine {

    override suspend fun executeWorkflow(workflowId: String, triggerType: String) {
        val workflow = workflowRepository.getWorkflowById(workflowId)
        if (workflow == null) {
            val errorMsg = "Workflow not found: $workflowId"
            Timber.e(errorMsg)
            val history = History(
                workflowId = workflowId,
                workflowName = "Unknown",
                executedAt = Date(),
                status = "Failure",
                logs = "ERROR: $errorMsg",
                triggerType = triggerType
            )
            historyRepository.insertHistory(history)
            return
        }
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
                var executor = moduleExecutorProvider.get(module.type)

                if (executor == null && module.type == "LOG_MESSAGE") {
                    executor = logMessageModule
                }

                if (executor == null) {
                    val errorMsg = "No executor found for module type: ${module.type}"
                    Timber.e(errorMsg)
                    Timber.e("Available module types: ${moduleExecutorProvider.getAvailableTypes().joinToString(", ")}")
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
                } catch (e: com.google.android.gms.auth.UserRecoverableAuthException) {
                    status = "Failure"
                    logBuilder.append("ERROR: Need remote consent - ${e.message}\n")
                    throw e
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
                logs = logBuilder.toString(),
                triggerType = triggerType
            )
            Timber.d("Saving execution history: workflowId=$workflowId, workflowName=${workflow.name}, status=$status, triggerType=$triggerType")
            historyRepository.insertHistory(history)
            Timber.d("Execution history saved successfully for workflow: ${workflow.name} (triggerType=$triggerType)")
        }
    }

    override suspend fun executeSingleModule(module: Module): ExecutionResult {
        val variables = mutableMapOf<String, Any>() // Empty context for single execution
        val context = ExecutionContext(module, variables)
        var executor = moduleExecutorProvider.get(module.type)

        if (executor == null && module.type == "LOG_MESSAGE") {
            executor = logMessageModule
        }

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
