package com.gws.auto.mobile.android.domain.engine.modules

import com.gws.auto.mobile.android.data.remote.OutlookApiService
import com.gws.auto.mobile.android.domain.engine.ExecutionContext
import com.gws.auto.mobile.android.domain.engine.ModuleExecutor
import com.gws.auto.mobile.android.domain.model.ExecutionResult
import javax.inject.Inject

/**
 * A workflow module for sending an email via Outlook using the Microsoft Graph API.
 */
class OutlookSendEmailModule @Inject constructor(
    private val outlookApiService: OutlookApiService
) : ModuleExecutor {

    override suspend fun execute(context: ExecutionContext): ExecutionResult {
        // Resolve variables for the email parameters from the execution context.
        val toRecipient = context.resolveVariables(context.module.parameters["to"] ?: "")
        val subject = context.resolveVariables(context.module.parameters["subject"] ?: "")
        val body = context.resolveVariables(context.module.parameters["body"] ?: "")

        // Basic validation to ensure required parameters are present.
        if (toRecipient.isBlank() || subject.isBlank()) {
            return ExecutionResult(false, "'to' and 'subject' parameters are required.")
        }

        // Delegate the email sending logic to the OutlookApiService.
        val success = outlookApiService.sendEmail(toRecipient, subject, body)

        // Return a result based on the outcome of the API call.
        return if (success) {
            ExecutionResult(true, "Email sent successfully to $toRecipient")
        } else {
            ExecutionResult(false, "Failed to send email to $toRecipient. User may need to sign in, or grant permissions.")
        }
    }
}
