package com.gws.auto.mobile.android.domain.engine.modules

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
                return ExecutionResult(false, "All source and target parameters are required.")
            }

            val sourceFileId = extractFileId(sourceSpreadsheetId)
            val targetFileId = extractFileId(targetSpreadsheetId)

            val fullSourceRange = "'$sourceSheetName'!$sourceRange"
            val valuesToCopy = sheetsApiService.getValues(sourceFileId, fullSourceRange)

            val fullTargetRange = "'$targetSheetName'!$targetCell"
            sheetsApiService.updateValues(targetFileId, fullTargetRange, valuesToCopy)

            Timber.d("Successfully copied values from $fullSourceRange to $fullTargetRange")
            ExecutionResult(true)
        } catch (e: Exception) {
            Timber.e(e, "Failed to copy and paste sheet values")
            ExecutionResult(false, e.message)
        }
    }

    private fun extractFileId(source: String): String {
        return "/d/([a-zA-Z0-9_-]+)".toRegex().find(source)?.groupValues?.get(1) ?: source
    }
}
