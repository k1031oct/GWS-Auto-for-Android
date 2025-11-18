package com.gws.auto.mobile.android.domain.engine.modules

import com.gws.auto.mobile.android.domain.engine.ExecutionContext
import com.gws.auto.mobile.android.domain.model.ExecutionResult
import com.gws.auto.mobile.android.domain.engine.ModuleExecutor
import timber.log.Timber
import javax.inject.Inject

class DefineVariableModule @Inject constructor() : ModuleExecutor {
    override suspend fun execute(context: ExecutionContext): ExecutionResult {
        val variableName = context.module.parameters["variableName"] ?: return ExecutionResult(false, "Missing variableName parameter")
        val value = context.resolveVariables(context.module.parameters["value"] ?: "")
        
        context.setVariable(variableName, value)
        Timber.d("Defined variable '$variableName' with value '$value'")
        return ExecutionResult(true)
    }
}
