package com.gws.auto.mobile.android.domain.engine.modules

import com.gws.auto.mobile.android.domain.engine.ExecutionContext
import com.gws.auto.mobile.android.domain.model.ExecutionResult
import com.gws.auto.mobile.android.domain.engine.ModuleExecutor
import com.gws.auto.mobile.android.domain.service.GmailApiService
import timber.log.Timber
import javax.inject.Inject

class GmailSendEmailModule @Inject constructor(
    private val gmailApiService: GmailApiService
) : ModuleExecutor {
    override suspend fun execute(context: ExecutionContext): ExecutionResult {
        return try {
            val to = context.resolveVariables(context.module.parameters["to"] ?: "")
            val subject = context.resolveVariables(context.module.parameters["subject"] ?: "")
            val body = context.resolveVariables(context.module.parameters["body"] ?: "")
            val cc = context.resolveVariables(context.module.parameters["cc"] ?: "")
            val bcc = context.resolveVariables(context.module.parameters["bcc"] ?: "")

            if (to.isBlank() || subject.isBlank()) {
                return ExecutionResult.Error("To and Subject fields are required.")
            }

            gmailApiService.sendEmail(to, cc, bcc, subject, body)
            ExecutionResult.Success("Email sent to $to successfully.")
        } catch (e: com.google.android.gms.auth.UserRecoverableAuthException) {
            throw e
        } catch (e: Exception) {
            Timber.e(e, "Failed to send email")
            ExecutionResult.Error("Failed to send email: ${e.message}")
        }
    }
}
