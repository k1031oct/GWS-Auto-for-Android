package com.gws.auto.mobile.android.domain.engine.modules

import com.gws.auto.mobile.android.domain.engine.ExecutionContext
import com.gws.auto.mobile.android.domain.engine.ModuleExecutor
import com.gws.auto.mobile.android.domain.model.ExecutionResult
import kotlinx.coroutines.delay
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class DelayModule @Inject constructor() : ModuleExecutor {
    override suspend fun execute(context: ExecutionContext): ExecutionResult {
        return try {
            val durationStr = context.resolveVariables(context.module.parameters["duration"] ?: "0")
            val unitStr = context.resolveVariables(context.module.parameters["unit"] ?: "SECONDS")
            
            val duration = durationStr.toLongOrNull() ?: 0L
            val unit = try {
                TimeUnit.valueOf(unitStr.uppercase())
            } catch (e: IllegalArgumentException) {
                TimeUnit.SECONDS
            }

            val millis = unit.toMillis(duration)
            
            if (millis > 0) {
                delay(millis)
            }

            ExecutionResult.Success("Delayed for $duration $unit")
        } catch (e: Exception) {
            Timber.e(e, "Failed to delay")
            ExecutionResult.Error("Failed to delay: ${e.message}")
        }
    }
}
