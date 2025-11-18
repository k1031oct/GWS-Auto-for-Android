package com.gws.auto.mobile.android.domain.engine.modules

import com.gws.auto.mobile.android.domain.engine.ExecutionContext
import com.gws.auto.mobile.android.domain.model.ExecutionResult
import com.gws.auto.mobile.android.domain.engine.ModuleExecutor
import com.gws.auto.mobile.android.domain.service.GmailApiService
import timber.log.Timber
import javax.inject.Inject

class CreateGmailDraftModule @Inject constructor(
    private val gmailApiService: GmailApiService
) : ModuleExecutor {
    override suspend fun execute(context: ExecutionContext): ExecutionResult {
        return try {
            val to = context.resolveVariables(context.module.parameters["to"] ?: "")
            val subject = context.resolveVariables(context.module.parameters["subject"] ?: "")
            val body = context.resolveVariables(context.module.parameters["body"] ?: "")

            if (to.isBlank() || subject.isBlank()) {
                return ExecutionResult(false, "To and Subject fields are required.")
            }

            val draft = gmailApiService.createDraft(to, subject, body)
            Timber.d("Successfully created draft with ID: ${draft.id}")
            ExecutionResult(true)
        } catch (e: Exception) {
            Timber.e(e, "Failed to create Gmail draft")
            ExecutionResult(false, e.message)
        }
    }
}
