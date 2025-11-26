package com.gws.auto.mobile.android.domain.engine.modules

import com.google.api.services.sheets.v4.model.ValueRange
import com.gws.auto.mobile.android.domain.engine.ExecutionContext
import com.gws.auto.mobile.android.domain.model.ExecutionResult
import com.gws.auto.mobile.android.domain.engine.ModuleExecutor
import com.gws.auto.mobile.android.domain.service.SheetsApiService
import timber.log.Timber
import javax.inject.Inject

class CopyPasteSheetValuesModule @Inject constructor(
    private val sheetsApiService: SheetsApiService
) : ModuleExecutor {
    override suspend fun execute(context: ExecutionContext): ExecutionResult {
        return try {
            val sourceSpreadsheetId = context.resolveVariables(context.module.parameters["sourceSpreadsheetId"] ?: "")
            val sourceSheetName = context.resolveVariables(context.module.parameters["sourceSheetName"] ?: "")
            val sourceRange = context.resolveVariables(context.module.parameters["sourceRange"] ?: "")
            val targetSpreadsheetId = context.resolveVariables(context.module.parameters["targetSpreadsheetId"] ?: "")
            val targetSheetName = context.resolveVariables(context.module.parameters["targetSheetName"] ?: "")
            val targetCell = context.resolveVariables(context.module.parameters["targetCell"] ?: "A1")

            if (sourceSpreadsheetId.isBlank() || sourceSheetName.isBlank() || sourceRange.isBlank() || targetSpreadsheetId.isBlank() || targetSheetName.isBlank()) {
                return ExecutionResult.Error("All source and target parameters are required.")
            }

            val sourceFileId = extractFileId(sourceSpreadsheetId)
            val targetFileId = extractFileId(targetSpreadsheetId)

            val fullSourceRange = "$sourceSheetName!$sourceRange"
            val fullTargetRange = "$targetSheetName!$targetCell"

            val values = sheetsApiService.getValues(sourceFileId, fullSourceRange)
            
            if (values.getValues().isNullOrEmpty()) {
                 return ExecutionResult.Error("No values found in source range.")
            }

            sheetsApiService.updateValues(targetFileId, fullTargetRange, values)

            ExecutionResult.Success("Copied values from $fullSourceRange to $fullTargetRange")
        } catch (e: com.google.android.gms.auth.UserRecoverableAuthException) {
            throw e
        } catch (e: Exception) {
            Timber.e(e, "Failed to copy/paste sheet values")
            ExecutionResult.Error("Failed to copy/paste values: ${e.message}")
        }
    }

    private fun extractFileId(urlOrId: String): String {
        return "/d/([a-zA-Z0-9_-]+)".toRegex().find(urlOrId)?.groupValues?.get(1) ?: urlOrId
    }
}
