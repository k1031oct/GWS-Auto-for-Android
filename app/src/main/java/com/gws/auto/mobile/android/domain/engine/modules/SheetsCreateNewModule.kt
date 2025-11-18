package com.gws.auto.mobile.android.domain.engine.modules

import com.gws.auto.mobile.android.domain.engine.ExecutionContext
import com.gws.auto.mobile.android.domain.model.ExecutionResult
import com.gws.auto.mobile.android.domain.engine.ModuleExecutor
import com.gws.auto.mobile.android.domain.service.SheetsApiService
import timber.log.Timber
import javax.inject.Inject

class SheetsCreateNewModule @Inject constructor(
    private val sheetsApiService: SheetsApiService
) : ModuleExecutor {
    override suspend fun execute(context: ExecutionContext): ExecutionResult {
        return try {
            val newFileName = context.resolveVariables(context.module.parameters["newFileName"] ?: "")
            val destFolderId = context.resolveVariables(context.module.parameters["destFolderId"] ?: "")

            if (newFileName.isBlank()) {
                return ExecutionResult(false, "New file name is required.")
            }

            val newSheet = sheetsApiService.createSpreadsheet(newFileName, destFolderId)
            val outputVar = context.module.parameters["outputSheetId"]
            if (outputVar != null) {
                context.setVariable(outputVar, newSheet.spreadsheetId)
            }

            ExecutionResult(true, "Successfully created new spreadsheet with ID: ${newSheet.spreadsheetId}")
        } catch (e: Exception) {
            Timber.e(e, "Failed to create new Google Sheet")
            ExecutionResult(false, e.message)
        }
    }
}
