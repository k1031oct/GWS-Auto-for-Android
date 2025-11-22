package com.gws.auto.mobile.android.domain.engine.modules

import android.content.Context
import android.widget.Toast
import com.gws.auto.mobile.android.domain.engine.ExecutionContext
import com.gws.auto.mobile.android.domain.engine.ModuleExecutor
import com.gws.auto.mobile.android.domain.model.ExecutionResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * A workflow module that displays a short Toast notification on the UI thread.
 */
class ToastNotificationModule @Inject constructor(
    @ApplicationContext private val context: Context
) : ModuleExecutor {

    override suspend fun execute(context: ExecutionContext): ExecutionResult {
        // Resolve the message to be displayed from the module parameters.
        val message = context.resolveVariables(context.module.parameters["message"] ?: "")

        if (message.isBlank()) {
            return ExecutionResult(false, "Message for Toast notification cannot be empty.")
        }

        // Switch to the Main dispatcher to show UI components like Toast.
        withContext(Dispatchers.Main) {
            Toast.makeText(this@ToastNotificationModule.context, message, Toast.LENGTH_SHORT).show()
        }

        // This module is generally considered successful once the Toast is shown.
        return ExecutionResult(true, "Toast notification displayed.")
    }
}
