package com.gws.auto.mobile.android.domain.engine.modules

import com.google.api.services.sheets.v4.model.DimensionRange
import com.google.api.services.sheets.v4.model.Request
import com.google.api.services.sheets.v4.model.InsertDimensionRequest
import com.gws.auto.mobile.android.domain.engine.ExecutionContext
import com.gws.auto.mobile.android.domain.engine.ModuleExecutor
import com.gws.auto.mobile.android.domain.model.ExecutionResult
import com.gws.auto.mobile.android.domain.service.SheetsApiService
import timber.log.Timber
import javax.inject.Inject

class SheetsInsertRowsColsModule @Inject constructor(
    private val sheetsApiService: SheetsApiService
) : ModuleExecutor {
    override suspend fun execute(context: ExecutionContext): ExecutionResult {
        return try {
            val spreadsheetId = context.resolveVariables(context.module.parameters["spreadsheetId"] ?: "")
            val sheetName = context.resolveVariables(context.module.parameters["sheetName"] ?: "")
            val dimension = context.resolveVariables(context.module.parameters["dimension"] ?: "ROWS") // ROWS or COLUMNS
            val insertAt = context.resolveVariables(context.module.parameters["insertAt"] ?: "1").toIntOrNull() ?: 1
            val count = context.resolveVariables(context.module.parameters["count"] ?: "1").toIntOrNull() ?: 1

            if (spreadsheetId.isBlank() || sheetName.isBlank()) {
                return ExecutionResult.Error("Spreadsheet ID and Sheet Name are required.")
            }

            val spreadsheet = sheetsApiService.getSpreadsheet(spreadsheetId)
            val sheet = spreadsheet.sheets.find { it.properties.title == sheetName }
                ?: return ExecutionResult.Error("Sheet '$sheetName' not found.")
            val sheetId = sheet.properties.sheetId

            // Convert 1-based to 0-based index
            val startIndex = if (insertAt > 0) insertAt - 1 else 0

            val requests = listOf(
                Request().setInsertDimension(
                    InsertDimensionRequest()
                        .setRange(DimensionRange()
                            .setSheetId(sheetId)
                            .setDimension(dimension)
                            .setStartIndex(startIndex)
                            .setEndIndex(startIndex + count)
                        )
                        .setInheritFromBefore(false) // Default to not inheriting style
                )
            )

            sheetsApiService.batchUpdate(spreadsheetId, requests)

            ExecutionResult.Success("Inserted $count $dimension(s) at index $insertAt in sheet: $sheetName")
        } catch (e: Exception) {
            Timber.e(e, "Failed to insert rows/cols")
            ExecutionResult.Error("Failed to insert rows/cols: ${e.message}")
        }
    }
}
