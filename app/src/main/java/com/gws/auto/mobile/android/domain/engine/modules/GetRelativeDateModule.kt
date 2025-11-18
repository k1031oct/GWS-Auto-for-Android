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
        try {
            val baseDateStr = context.resolveVariables(context.module.parameters["baseDate"] ?: "TODAY")
            val offsetValue = context.resolveVariables(context.module.parameters["offsetValue"] ?: "0").toLong()
            val offsetUnit = context.resolveVariables(context.module.parameters["offsetUnit"] ?: "DAYS")
            val outputVariableName = context.module.parameters["outputVariableName"] ?: return ExecutionResult(false, "Missing outputVariableName parameter")

            val baseDate = if (baseDateStr.equals("TODAY", ignoreCase = true)) {
                LocalDate.now()
            } else {
                LocalDate.parse(baseDateStr, DateTimeFormatter.ISO_LOCAL_DATE)
            }

            val chronoUnit = when (offsetUnit.uppercase()) {
                "DAYS" -> ChronoUnit.DAYS
                "WEEKS" -> ChronoUnit.WEEKS
                "MONTHS" -> ChronoUnit.MONTHS
                "YEARS" -> ChronoUnit.YEARS
                else -> return ExecutionResult(false, "Invalid offsetUnit: $offsetUnit")
            }

            val resultDate = baseDate.plus(offsetValue, chronoUnit)
            val formattedDate = resultDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
            context.setVariable(outputVariableName, formattedDate)
            Timber.d("Calculated date: $formattedDate, saved to '$outputVariableName'")
            return ExecutionResult(true)
        } catch (e: Exception) {
            Timber.e(e, "Failed to execute GetRelativeDateModule")
            return ExecutionResult(false, e.message)
        }
    }
}
