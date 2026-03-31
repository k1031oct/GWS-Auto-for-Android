package com.gws.auto.mobile.android.domain.engine.modules

import com.gws.auto.mobile.android.domain.engine.ExecutionContext
import com.gws.auto.mobile.android.domain.engine.ModuleExecutor
import com.gws.auto.mobile.android.domain.model.ExecutionResult
import com.gws.auto.mobile.android.domain.service.DriveApiService
import com.gws.auto.mobile.android.domain.service.SheetsApiService
import timber.log.Timber
import javax.inject.Inject

class DriveListFilesToSheetModule @Inject constructor(
    private val driveApiService: DriveApiService,
    private val sheetsApiService: SheetsApiService
) : ModuleExecutor {
    override suspend fun execute(context: ExecutionContext): ExecutionResult {
        return try {
            val folderId = context.resolveVariables(context.module.parameters["folderId"] ?: "")
            val spreadsheetId = context.resolveVariables(context.module.parameters["spreadsheetId"] ?: "")
            val sheetName = context.resolveVariables(context.module.parameters["sheetName"] ?: "")

            if (folderId.isBlank() || spreadsheetId.isBlank() || sheetName.isBlank()) {
                return ExecutionResult.Error("Folder ID, Spreadsheet ID, and Sheet Name are required.")
            }

            val fileList = driveApiService.listFiles(folderId)
            val files = fileList.files ?: emptyList()

            if (files.isEmpty()) {
                return ExecutionResult.Success("No files found in folder: $folderId")
            }

            val rows = files.map { file ->
                listOf(
                    file.name ?: "",
                    file.webViewLink ?: ""
                )
            }

            sheetsApiService.appendRows(spreadsheetId, sheetName, rows)

            ExecutionResult.Success("Listed ${files.size} files to sheet: $sheetName")
        } catch (e: Exception) {
            Timber.e(e, "Failed to list files to sheet")
            ExecutionResult.Error("Failed to list files: ${e.message}")
        }
    }
}
