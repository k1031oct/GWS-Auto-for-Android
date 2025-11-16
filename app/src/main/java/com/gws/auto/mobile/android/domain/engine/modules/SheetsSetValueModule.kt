package com.gws.auto.mobile.android.domain.engine.modules

import com.google.api.services.sheets.v4.model.ValueRange
import com.gws.auto.mobile.android.domain.engine.ExecutionContext
import com.gws.auto.mobile.android.domain.engine.ExecutionResult
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
                return ExecutionResult(false, "Target URL, sheet, and cell are required.")
            }

            val spreadsheetId = extractFileId(targetUrl)
            val valueRange = ValueRange().setValues(listOf(listOf(valueToSet)))
            sheetsApiService.updateValues(spreadsheetId, "'$targetSheet'!$targetCell", valueRange)

            ExecutionResult(true, "Successfully set value in cell $targetCell")
        } catch (e: Exception) {
            Timber.e(e, "Failed to set value in Google Sheet")
            ExecutionResult(false, e.message)
        }
    }

    private fun extractFileId(source: String): String {
        return "/d/([a-zA-Z0-9_-]+)".toRegex().find(source)?.groupValues?.get(1) ?: source
    }
}
