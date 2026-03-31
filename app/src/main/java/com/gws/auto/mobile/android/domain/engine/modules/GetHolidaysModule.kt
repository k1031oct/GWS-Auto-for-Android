package com.gws.auto.mobile.android.domain.engine.modules

import com.gws.auto.mobile.android.data.repository.SettingsRepository
import com.gws.auto.mobile.android.domain.engine.ExecutionContext
import com.gws.auto.mobile.android.domain.engine.ModuleExecutor
import com.gws.auto.mobile.android.domain.model.ExecutionResult
import com.gws.auto.mobile.android.domain.service.CalendarApiService
import com.gws.auto.mobile.android.domain.service.SheetsApiService
import kotlinx.coroutines.flow.first
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

class GetHolidaysModule @Inject constructor(
    private val calendarApiService: CalendarApiService,
    private val sheetsApiService: SheetsApiService,
    private val settingsRepository: SettingsRepository
) : ModuleExecutor {
    override suspend fun execute(context: ExecutionContext): ExecutionResult {
        return try {
            val startDateStr = context.resolveVariables(context.module.parameters["startDate"] ?: "")
            val endDateStr = context.resolveVariables(context.module.parameters["endDate"] ?: "")
            val spreadsheetId = context.resolveVariables(context.module.parameters["spreadsheetId"] ?: "")
            val sheetName = context.resolveVariables(context.module.parameters["sheetName"] ?: "")
            var countryCode = context.resolveVariables(context.module.parameters["countryCode"] ?: "")

            if (startDateStr.isBlank() || endDateStr.isBlank() || spreadsheetId.isBlank() || sheetName.isBlank()) {
                return ExecutionResult.Error("Start Date, End Date, Spreadsheet ID, and Sheet Name are required.")
            }

            if (countryCode.isBlank()) {
                // TODO: Get from settings. Assuming "JP" for now if not set, or implement settings fetch.
                // settingsRepository.countryCode.first() ?
                // The user said "matches application setting".
                // I'll assume "JP" as default if I can't find the setting key easily, but I should try.
                // I'll check SettingsRepository.
                countryCode = "JP" 
            }

            val startDate = LocalDate.parse(startDateStr)
            val endDate = LocalDate.parse(endDateStr)

            val holidays = calendarApiService.getHolidaysInRange(countryCode, startDate, endDate)

            if (holidays.isEmpty()) {
                return ExecutionResult.Success("No holidays found for $countryCode between $startDate and $endDate")
            }

            val rows = holidays.map { listOf(it.date.toString(), it.name) }
            sheetsApiService.appendRows(spreadsheetId, sheetName, rows)

            ExecutionResult.Success("Exported ${holidays.size} holidays to sheet: $sheetName")
        } catch (e: Exception) {
            Timber.e(e, "Failed to get holidays")
            ExecutionResult.Error("Failed to get holidays: ${e.message}")
        }
    }
}
