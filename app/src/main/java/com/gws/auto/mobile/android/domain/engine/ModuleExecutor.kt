package com.gws.auto.mobile.android.domain.engine

import com.gws.auto.mobile.android.domain.model.ExecutionResult

/**
 * An interface for a single executable module.
 */
interface ModuleExecutor {
    suspend fun execute(context: ExecutionContext): ExecutionResult
}
