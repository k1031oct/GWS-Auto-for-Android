package com.gws.auto.mobile.android.domain.engine.modules

import com.gws.auto.mobile.android.domain.engine.ExecutionContext
import com.gws.auto.mobile.android.domain.model.ExecutionResult
import com.gws.auto.mobile.android.domain.engine.ModuleExecutor
import javax.inject.Inject

class LogMessageModule @Inject constructor() : ModuleExecutor {
    override suspend fun execute(context: ExecutionContext): ExecutionResult {
        val message = context.resolveVariables(context.module.parameters["message"] ?: "")
        return ExecutionResult.Success(message)
    }
}
