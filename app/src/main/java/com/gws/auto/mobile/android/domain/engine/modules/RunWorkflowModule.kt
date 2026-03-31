package com.gws.auto.mobile.android.domain.engine.modules

import com.gws.auto.mobile.android.domain.engine.ExecutionContext
import com.gws.auto.mobile.android.domain.engine.ModuleExecutor
import com.gws.auto.mobile.android.domain.model.ExecutionResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class RunWorkflowModule @Inject constructor() : ModuleExecutor {
    override suspend fun execute(context: ExecutionContext): ExecutionResult {
        return try {
            val workflowId = context.resolveVariables(context.module.parameters["workflowId"] ?: "")
            val mode = context.resolveVariables(context.module.parameters["mode"] ?: "SEQUENTIAL") // SEQUENTIAL or PARALLEL

            if (workflowId.isBlank()) {
                return ExecutionResult.Error("Workflow ID is required.")
            }

            val engine = context.workflowEngine
            if (engine == null) {
                return ExecutionResult.Error("Workflow Engine not available in context.")
            }

            if (mode.equals("PARALLEL", ignoreCase = true)) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        engine.executeWorkflow(workflowId, "SUB_WORKFLOW_PARALLEL")
                    } catch (e: Exception) {
                        Timber.e(e, "Failed to execute parallel workflow: $workflowId")
                    }
                }
                ExecutionResult.Success("Started workflow $workflowId in parallel.")
            } else {
                val success = engine.executeWorkflow(workflowId, "SUB_WORKFLOW_SEQUENTIAL")
                if (success) {
                    ExecutionResult.Success("Executed workflow $workflowId successfully.")
                } else {
                    ExecutionResult.Error("Workflow $workflowId failed.")
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to run workflow")
            ExecutionResult.Error("Failed to run workflow: ${e.message}")
        }
    }
}
