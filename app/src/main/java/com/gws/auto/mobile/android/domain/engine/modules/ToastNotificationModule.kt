package com.gws.auto.mobile.android.domain.engine.modules

import android.widget.Toast
import com.gws.auto.mobile.android.MainApplication
import com.gws.auto.mobile.android.domain.engine.ExecutionContext
import com.gws.auto.mobile.android.domain.engine.ModuleExecutor
import com.gws.auto.mobile.android.domain.model.ExecutionResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * A workflow module that displays a short Toast notification on the UI thread.
 */
class ToastNotificationModule @Inject constructor() : ModuleExecutor {

    override suspend fun execute(context: ExecutionContext): ExecutionResult {
        // Resolve the message to be displayed from the module parameters.
        val message = context.resolveVariables(context.module.parameters["message"] ?: "")

        val toastMessage = if (message.isBlank()) {
            "Message for Toast notification is empty."
        } else {
            message
        }

        // Switch to the Main dispatcher to show UI components like Toast.
        withContext(Dispatchers.Main) {
            MainApplication.currentActivity?.get()?.let {
                Toast.makeText(it, toastMessage, Toast.LENGTH_SHORT).show()
            }
        }

        // This module is generally considered successful once the Toast is shown.
        return ExecutionResult(true, "Toast notification displayed.")
    }
}
