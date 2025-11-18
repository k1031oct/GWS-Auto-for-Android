package com.gws.auto.mobile.android.domain.engine.modules

import com.gws.auto.mobile.android.domain.engine.ExecutionContext
import com.gws.auto.mobile.android.domain.model.ExecutionResult
import com.gws.auto.mobile.android.domain.engine.ModuleExecutor
import com.gws.auto.mobile.android.domain.service.SheetsApiService
import timber.log.Timber
import javax.inject.Inject

class SheetsClearValuesModule @Inject constructor(
    private val sheetsApiService: SheetsApiService
) : ModuleExecutor {
    override suspend fun execute(context: ExecutionContext): ExecutionResult {
        return try {
            val targetUrl = context.resolveVariables(context.module.parameters["targetUrl"] ?: "")
            val targetSheet = context.resolveVariables(context.module.parameters["targetSheet"] ?: "")
            val targetRange = context.resolveVariables(context.module.parameters["targetRange"] ?: "")

            if (targetUrl.isBlank() || targetSheet.isBlank() || targetRange.isBlank()) {
                return ExecutionResult(false, "Target URL, sheet, and range are required.")
            }

            val spreadsheetId = extractFileId(targetUrl)
            sheetsApiService.clearValues(spreadsheetId, "'$targetSheet'!$targetRange")

            ExecutionResult(true, "Successfully cleared values in range $targetRange")
        } catch (e: Exception) {
            Timber.e(e, "Failed to clear values in Google Sheet")
            ExecutionResult(false, e.message)
        }
    }

    private fun extractFileId(source: String): String {
        return "/d/([a-zA-Z0-9_-]+)".toRegex().find(source)?.groupValues?.get(1) ?: source
    }
}
