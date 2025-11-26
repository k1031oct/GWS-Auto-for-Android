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
                return ExecutionResult.Error("Spreadsheet URL and row data are required.")
            }

            val spreadsheetId = extractFileId(spreadsheetUrl)
            val values = rowData.split(",").map { it.trim() }

            sheetsApiService.appendRow(spreadsheetId, sheetName, values)

            ExecutionResult.Success("Appended row to $sheetName")
        } catch (e: com.google.android.gms.auth.UserRecoverableAuthException) {
            throw e
        } catch (e: Exception) {
            Timber.e(e, "Failed to append row to Google Sheet")
            ExecutionResult.Error("Failed to append row: ${e.message}")
        }
    }

    private fun extractFileId(urlOrId: String): String {
        return "/d/([a-zA-Z0-9_-]+)".toRegex().find(urlOrId)?.groupValues?.get(1) ?: urlOrId
    }
}
