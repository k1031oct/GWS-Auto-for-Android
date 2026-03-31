package com.gws.auto.mobile.android.domain.engine.modules

import com.google.api.services.sheets.v4.model.DimensionRange
import com.google.api.services.sheets.v4.model.Request
import com.google.api.services.sheets.v4.model.UpdateDimensionPropertiesRequest
import com.google.api.services.sheets.v4.model.DimensionProperties
import com.gws.auto.mobile.android.domain.engine.ExecutionContext
import com.gws.auto.mobile.android.domain.engine.ModuleExecutor
import com.gws.auto.mobile.android.domain.model.ExecutionResult
import com.gws.auto.mobile.android.domain.service.SheetsApiService
import timber.log.Timber
import javax.inject.Inject

class SheetsUnhideRowsColsModule @Inject constructor(
    private val sheetsApiService: SheetsApiService
) : ModuleExecutor {
    override suspend fun execute(context: ExecutionContext): ExecutionResult {
        return try {
            val spreadsheetId = context.resolveVariables(context.module.parameters["spreadsheetId"] ?: "")
            val sheetName = context.resolveVariables(context.module.parameters["sheetName"] ?: "")

            if (spreadsheetId.isBlank() || sheetName.isBlank()) {
                return ExecutionResult.Error("Spreadsheet ID and Sheet Name are required.")
            }

            val spreadsheet = sheetsApiService.getSpreadsheet(spreadsheetId)
            val sheet = spreadsheet.sheets.find { it.properties.title == sheetName }
                ?: return ExecutionResult.Error("Sheet '$sheetName' not found.")
            
            val sheetId = sheet.properties.sheetId

            val requests = listOf(
                Request().setUpdateDimensionProperties(
                    UpdateDimensionPropertiesRequest()
                        .setRange(DimensionRange().setSheetId(sheetId).setDimension("ROWS"))
                        .setProperties(DimensionProperties().setHiddenByUser(false))
                        .setFields("hiddenByUser")
                ),
                Request().setUpdateDimensionProperties(
                    UpdateDimensionPropertiesRequest()
                        .setRange(DimensionRange().setSheetId(sheetId).setDimension("COLUMNS"))
                        .setProperties(DimensionProperties().setHiddenByUser(false))
                        .setFields("hiddenByUser")
                )
            )

            sheetsApiService.batchUpdate(spreadsheetId, requests)

            ExecutionResult.Success("Unhid all rows and columns in sheet: $sheetName")
        } catch (e: Exception) {
            Timber.e(e, "Failed to unhide rows/cols")
            ExecutionResult.Error("Failed to unhide rows/cols: ${e.message}")
        }
    }
}
