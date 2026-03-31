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

class SheetsHideRowsColsModule @Inject constructor(
    private val sheetsApiService: SheetsApiService
) : ModuleExecutor {
    override suspend fun execute(context: ExecutionContext): ExecutionResult {
        return try {
            val spreadsheetId = context.resolveVariables(context.module.parameters["spreadsheetId"] ?: "")
            val sheetName = context.resolveVariables(context.module.parameters["sheetName"] ?: "")
            val dimension = context.resolveVariables(context.module.parameters["dimension"] ?: "ROWS") // ROWS or COLUMNS
            val checkRange = context.resolveVariables(context.module.parameters["checkRange"] ?: "") // e.g. "A:A" or "1:1"
            val condition = context.resolveVariables(context.module.parameters["condition"] ?: "EMPTY") // EMPTY, EQUALS, CONTAINS
            val conditionValue = context.resolveVariables(context.module.parameters["conditionValue"] ?: "")

            if (spreadsheetId.isBlank() || sheetName.isBlank() || checkRange.isBlank()) {
                return ExecutionResult.Error("Spreadsheet ID, Sheet Name, and Check Range are required.")
            }

            val spreadsheet = sheetsApiService.getSpreadsheet(spreadsheetId)
            val sheet = spreadsheet.sheets.find { it.properties.title == sheetName }
                ?: return ExecutionResult.Error("Sheet '$sheetName' not found.")
            val sheetId = sheet.properties.sheetId

            // Get values to check
            val fullRange = "'$sheetName'!$checkRange"
            val response = sheetsApiService.getValues(spreadsheetId, fullRange)
            val values: List<List<Any>> = response.values as? List<List<Any>> ?: emptyList()

            val requests = mutableListOf<Request>()
            
            // Assuming checkRange is 1D (single column or single row)
            // If dimension is ROWS, we are checking a Column (values are List<List<Any>> where inner list is size 1)
            // If dimension is COLUMNS, we are checking a Row (values is List<List<Any>> size 1, inner list is size N)

            val indicesToHide = mutableListOf<Int>()

            if (dimension == "ROWS") {
                // Checking a column, hiding rows
                values.forEachIndexed { index, row ->
                    val cellValue = row.firstOrNull()?.toString() ?: ""
                    if (checkCondition(cellValue, condition, conditionValue)) {
                        indicesToHide.add(index)
                    }
                }
            } else {
                // Checking a row, hiding columns
                val row: List<Any> = if (values.isNotEmpty()) values[0] else emptyList()
                for (index in row.indices) {
                    val cell = row[index]
                    val cellValue = cell.toString()
                    if (checkCondition(cellValue, condition, conditionValue)) {
                        indicesToHide.add(index)
                    }
                }
            }

            // Group indices into ranges to minimize requests
            if (indicesToHide.isNotEmpty()) {
                val ranges = groupIndices(indicesToHide)
                ranges.forEach { (start, end) ->
                    requests.add(
                        Request().setUpdateDimensionProperties(
                            UpdateDimensionPropertiesRequest()
                                .setRange(DimensionRange()
                                    .setSheetId(sheetId)
                                    .setDimension(dimension)
                                    .setStartIndex(start)
                                    .setEndIndex(end + 1) // End index is exclusive
                                )
                                .setProperties(DimensionProperties().setHiddenByUser(true))
                                .setFields("hiddenByUser")
                        )
                    )
                }
                sheetsApiService.batchUpdate(spreadsheetId, requests)
            }

            ExecutionResult.Success("Hidden ${indicesToHide.size} $dimension(s) in sheet: $sheetName")
        } catch (e: Exception) {
            Timber.e(e, "Failed to hide rows/cols")
            ExecutionResult.Error("Failed to hide rows/cols: ${e.message}")
        }
    }

    private fun checkCondition(value: String, condition: String, target: String): Boolean {
        return when (condition) {
            "EMPTY" -> value.isBlank()
            "NOT_EMPTY" -> value.isNotBlank()
            "EQUALS" -> value == target
            "CONTAINS" -> value.contains(target, ignoreCase = true)
            else -> false
        }
    }

    private fun groupIndices(indices: List<Int>): List<Pair<Int, Int>> {
        if (indices.isEmpty()) return emptyList()
        val sorted = indices.sorted()
        val ranges = mutableListOf<Pair<Int, Int>>()
        var start = sorted[0]
        var end = sorted[0]

        for (i in 1 until sorted.size) {
            if (sorted[i] == end + 1) {
                end = sorted[i]
            } else {
                ranges.add(start to end)
                start = sorted[i]
                end = sorted[i]
            }
        }
        ranges.add(start to end)
        return ranges
    }
}
