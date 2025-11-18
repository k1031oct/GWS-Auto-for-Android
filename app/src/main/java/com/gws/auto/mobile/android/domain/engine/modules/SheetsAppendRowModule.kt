package com.gws.auto.mobile.android.domain.engine.modules

import com.gws.auto.mobile.android.domain.engine.ExecutionContext
import com.gws.auto.mobile.android.domain.model.ExecutionResult
import com.gws.auto.mobile.android.domain.engine.ModuleExecutor
import com.gws.auto.mobile.android.domain.service.SheetsApiService
import timber.log.Timber
import javax.inject.Inject

class SheetsAppendRowModule @Inject constructor(
    private val sheetsApiService: SheetsApiService
) : ModuleExecutor {
    override suspend fun execute(context: ExecutionContext): ExecutionResult {
        return try {
            val spreadsheetUrl = context.resolveVariables(context.module.parameters["spreadsheetUrl"] ?: "")
            val sheetName = context.resolveVariables(context.module.parameters["sheetName"] ?: "")
            val rowData = context.resolveVariables(context.module.parameters["rowData"] ?: "")

            if (spreadsheetUrl.isBlank() || rowData.isBlank()) {
                return ExecutionResult(false, "Spreadsheet URL and row data are required.")
            }

            val spreadsheetId = extractFileId(spreadsheetUrl)
            val values = rowData.split(",").map { it.trim() }
            sheetsApiService.appendRow(spreadsheetId, sheetName, values)

            ExecutionResult(true, "Successfully appended row to sheet.")
        } catch (e: Exception) {
            Timber.e(e, "Failed to append row to Google Sheet")
            ExecutionResult(false, e.message)
        }
    }

    private fun extractFileId(source: String): String {
        return "/d/([a-zA-Z0-9_-]+)".toRegex().find(source)?.groupValues?.get(1) ?: source
    }
}
