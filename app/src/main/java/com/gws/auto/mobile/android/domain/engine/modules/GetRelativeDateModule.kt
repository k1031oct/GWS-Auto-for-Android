package com.gws.auto.mobile.android.domain.engine.modules

import com.gws.auto.mobile.android.domain.engine.ExecutionContext
import com.gws.auto.mobile.android.domain.model.ExecutionResult
import com.gws.auto.mobile.android.domain.engine.ModuleExecutor
import timber.log.Timber
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class GetRelativeDateModule @Inject constructor() : ModuleExecutor {
    override suspend fun execute(context: ExecutionContext): ExecutionResult {
        return try {
            val baseDateStr = context.resolveVariables(context.module.parameters["baseDate"] ?: "")
            val offsetValueStr = context.resolveVariables(context.module.parameters["offsetValue"] ?: "0")
            val offsetUnit = context.resolveVariables(context.module.parameters["offsetUnit"] ?: "DAYS")
            val outputVariableName = context.module.parameters["outputVariableName"]

            if (baseDateStr.isBlank() || outputVariableName.isNullOrBlank()) {
                return ExecutionResult.Error("Base date and output variable name are required.")
            }

            val baseDate = LocalDate.parse(baseDateStr, DateTimeFormatter.ISO_LOCAL_DATE)
            val offsetValue = offsetValueStr.toLongOrNull() ?: 0L

            val chronoUnit = when (offsetUnit.uppercase()) {
                "DAYS" -> ChronoUnit.DAYS
                "WEEKS" -> ChronoUnit.WEEKS
                "MONTHS" -> ChronoUnit.MONTHS
                "YEARS" -> ChronoUnit.YEARS
                else -> return ExecutionResult.Error("Invalid offsetUnit: $offsetUnit")
            }

            val resultDate = baseDate.plus(offsetValue, chronoUnit)
            val formattedDate = resultDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
            context.setVariable(outputVariableName, formattedDate)
            Timber.d("Calculated date: $formattedDate, saved to '$outputVariableName'")
            
            ExecutionResult.Success("Date calculated: $formattedDate", mapOf(outputVariableName to formattedDate))
        } catch (e: Exception) {
            Timber.e(e, "Failed to execute GetRelativeDateModule")
            ExecutionResult.Error("Failed to calculate date: ${e.message}")
        }
    }
}
