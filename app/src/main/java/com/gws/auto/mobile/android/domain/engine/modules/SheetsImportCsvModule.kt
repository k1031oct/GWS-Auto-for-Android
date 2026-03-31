package com.gws.auto.mobile.android.domain.engine.modules

import com.gws.auto.mobile.android.domain.engine.ExecutionContext
import com.gws.auto.mobile.android.domain.engine.ModuleExecutor
import com.gws.auto.mobile.android.domain.model.ExecutionResult
import com.gws.auto.mobile.android.domain.service.DriveApiService
import com.gws.auto.mobile.android.domain.service.SheetsApiService
import timber.log.Timber
import javax.inject.Inject

class SheetsImportCsvModule @Inject constructor(
    private val driveApiService: DriveApiService,
    private val sheetsApiService: SheetsApiService
) : ModuleExecutor {
    override suspend fun execute(context: ExecutionContext): ExecutionResult {
        return try {
            val csvFileId = context.resolveVariables(context.module.parameters["csvFileId"] ?: "")
            val spreadsheetId = context.resolveVariables(context.module.parameters["spreadsheetId"] ?: "")
            val sheetName = context.resolveVariables(context.module.parameters["sheetName"] ?: "")
            val delimiter = context.resolveVariables(context.module.parameters["delimiter"] ?: ",")

            if (csvFileId.isBlank() || spreadsheetId.isBlank() || sheetName.isBlank()) {
                return ExecutionResult.Error("CSV File ID, Spreadsheet ID, and Sheet Name are required.")
            }

            val csvContent = driveApiService.getFileContent(csvFileId)
            val rows = parseCsv(csvContent, delimiter)

            if (rows.isNotEmpty()) {
                sheetsApiService.appendRows(spreadsheetId, sheetName, rows)
            }

            ExecutionResult.Success("Imported CSV to sheet: $sheetName")
        } catch (e: Exception) {
            Timber.e(e, "Failed to import CSV")
            ExecutionResult.Error("Failed to import CSV: ${e.message}")
        }
    }

    private fun parseCsv(content: String, delimiter: String): List<List<String>> {
        val rows = mutableListOf<List<String>>()
        val lines = content.lines()
        
        for (line in lines) {
            if (line.isBlank()) continue
            // Basic splitting, does not handle quoted values with delimiters
            // For a robust implementation, a CSV library is recommended
            val cells = line.split(delimiter).map { it.trim() }
            rows.add(cells)
        }
        return rows
    }
}
