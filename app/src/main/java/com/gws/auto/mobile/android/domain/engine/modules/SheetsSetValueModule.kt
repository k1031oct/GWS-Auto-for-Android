package com.gws.auto.mobile.android.domain.engine.modules

import com.google.api.services.sheets.v4.model.ValueRange
import com.gws.auto.mobile.android.domain.engine.ExecutionContext
import com.gws.auto.mobile.android.domain.model.ExecutionResult
import com.gws.auto.mobile.android.domain.engine.ModuleExecutor
import com.gws.auto.mobile.android.domain.service.SheetsApiService
import timber.log.Timber
import javax.inject.Inject

class SheetsSetValueModule @Inject constructor(
    private val sheetsApiService: SheetsApiService
) : ModuleExecutor {
    override suspend fun execute(context: ExecutionContext): ExecutionResult {
        return try {
            val targetUrl = context.resolveVariables(context.module.parameters["targetUrl"] ?: "")
            val targetSheet = context.resolveVariables(context.module.parameters["targetSheet"] ?: "")
            val targetCell = context.resolveVariables(context.module.parameters["targetCell"] ?: "")
            val valueToSet = context.resolveVariables(context.module.parameters["valueToSet"] ?: "")

            if (targetUrl.isBlank() || targetSheet.isBlank() || targetCell.isBlank()) {
                return ExecutionResult.Error("Target URL, sheet, and cell are required.")
            }

            val spreadsheetId = extractFileId(targetUrl)
            val fullRange = "$targetSheet!$targetCell"
            
            val valueRange = ValueRange().setValues(listOf(listOf(valueToSet)))

            sheetsApiService.updateValues(spreadsheetId, fullRange, valueRange)

            ExecutionResult.Success("Set value '$valueToSet' in $fullRange")
        } catch (e: com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException) {
            throw e.cause ?: e
        } catch (e: com.google.android.gms.auth.UserRecoverableAuthException) {
            throw e
        } catch (e: Exception) {
            Timber.e(e, "Failed to set value in Google Sheet")
            ExecutionResult.Error("Failed to set value: ${e.message}")
        }
    }

    private fun extractFileId(urlOrId: String): String {
        return "/d/([a-zA-Z0-9_-]+)".toRegex().find(urlOrId)?.groupValues?.get(1) ?: urlOrId
    }
}
