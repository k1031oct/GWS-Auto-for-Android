package com.gws.auto.mobile.android.domain.engine.modules

import com.gws.auto.mobile.android.domain.engine.ExecutionContext
import com.gws.auto.mobile.android.domain.engine.ModuleExecutor
import com.gws.auto.mobile.android.domain.model.ExecutionResult
import com.gws.auto.mobile.android.domain.service.DriveApiService
import com.gws.auto.mobile.android.domain.service.SheetsApiService
import timber.log.Timber
import java.util.Base64
import javax.inject.Inject

class SheetsExportPdfModule @Inject constructor(
    private val sheetsApiService: SheetsApiService,
    private val driveApiService: DriveApiService
) : ModuleExecutor {
    override suspend fun execute(context: ExecutionContext): ExecutionResult {
        return try {
            val spreadsheetId = context.resolveVariables(context.module.parameters["spreadsheetId"] ?: "")
            val sheetName = context.resolveVariables(context.module.parameters["sheetName"] ?: "")
            val destFolderId = context.resolveVariables(context.module.parameters["destFolderId"] ?: "")
            val fileName = context.resolveVariables(context.module.parameters["fileName"] ?: "")

            if (spreadsheetId.isBlank() || sheetName.isBlank() || destFolderId.isBlank() || fileName.isBlank()) {
                return ExecutionResult.Error("Spreadsheet ID, Sheet Name, Destination Folder ID, and File Name are required.")
            }

            val spreadsheet = sheetsApiService.getSpreadsheet(spreadsheetId)
            val sheet = spreadsheet.sheets.find { it.properties.title == sheetName }
                ?: return ExecutionResult.Error("Sheet '$sheetName' not found.")
            val sheetId = sheet.properties.sheetId

            val pdfBase64 = sheetsApiService.exportSheetToPdf(spreadsheetId, sheetId, destFolderId, fileName)
            val pdfBytes = Base64.getDecoder().decode(pdfBase64)

            val file = driveApiService.createFile(fileName, destFolderId, pdfBytes, "application/pdf")

            ExecutionResult.Success("Exported sheet '$sheetName' to PDF: ${file.webViewLink}", mapOf("pdfFileId" to file.id))
        } catch (e: Exception) {
            Timber.e(e, "Failed to export PDF")
            ExecutionResult.Error("Failed to export PDF: ${e.message}")
        }
    }
}
