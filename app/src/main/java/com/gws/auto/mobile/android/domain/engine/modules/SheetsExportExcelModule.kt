package com.gws.auto.mobile.android.domain.engine.modules

import com.gws.auto.mobile.android.domain.engine.ExecutionContext
import com.gws.auto.mobile.android.domain.engine.ModuleExecutor
import com.gws.auto.mobile.android.domain.model.ExecutionResult
import com.gws.auto.mobile.android.domain.service.DriveApiService
import timber.log.Timber
import javax.inject.Inject

class SheetsExportExcelModule @Inject constructor(
    private val driveApiService: DriveApiService
) : ModuleExecutor {
    override suspend fun execute(context: ExecutionContext): ExecutionResult {
        return try {
            val spreadsheetId = context.resolveVariables(context.module.parameters["spreadsheetId"] ?: "")
            val destFolderId = context.resolveVariables(context.module.parameters["destFolderId"] ?: "")
            val fileName = context.resolveVariables(context.module.parameters["fileName"] ?: "")

            if (spreadsheetId.isBlank() || destFolderId.isBlank() || fileName.isBlank()) {
                return ExecutionResult.Error("Spreadsheet ID, Destination Folder ID, and File Name are required.")
            }

            val file = driveApiService.exportToDrive(
                spreadsheetId, 
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", 
                destFolderId, 
                fileName
            )

            ExecutionResult.Success("Exported spreadsheet to Excel: ${file.webViewLink}", mapOf("excelFileId" to file.id))
        } catch (e: Exception) {
            Timber.e(e, "Failed to export Excel")
            ExecutionResult.Error("Failed to export Excel: ${e.message}")
        }
    }
}
