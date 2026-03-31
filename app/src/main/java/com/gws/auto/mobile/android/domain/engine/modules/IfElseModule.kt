package com.gws.auto.mobile.android.domain.engine.modules

import com.gws.auto.mobile.android.domain.engine.ExecutionContext
import com.gws.auto.mobile.android.domain.engine.ModuleExecutor
import com.gws.auto.mobile.android.domain.model.ExecutionResult
import timber.log.Timber
import javax.inject.Inject

class IfElseModule @Inject constructor() : ModuleExecutor {
    override suspend fun execute(context: ExecutionContext): ExecutionResult {
        return try {
            val condition = context.module.parameters["condition"] ?: ""
            val trueModuleId = context.module.parameters["trueModuleId"]
            val falseModuleId = context.module.parameters["falseModuleId"]

            if (condition.isBlank()) {
                return ExecutionResult.Error("Condition is required.")
            }

            // Resolve variables in condition
            val resolvedCondition = context.resolveVariables(condition)
            val result = evaluateCondition(resolvedCondition)

            if (result) {
                if (!trueModuleId.isNullOrBlank()) {
                    context.setNextModuleId(trueModuleId)
                }
                ExecutionResult.Success("Condition matched (True). Jumping to: $trueModuleId")
            } else {
                if (!falseModuleId.isNullOrBlank()) {
                    context.setNextModuleId(falseModuleId)
                }
                ExecutionResult.Success("Condition not matched (False). Jumping to: $falseModuleId")
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to evaluate condition")
            ExecutionResult.Error("Failed to evaluate condition: ${e.message}")
        }
    }

    private fun evaluateCondition(condition: String): Boolean {
        // Simple parser: "value1 OPERATOR value2"
        // Operators: ==, !=, >, <, >=, <=, CONTAINS
        // If no operator, check if string is "true" (case insensitive)
        
        val operators = listOf("==", "!=", ">=", "<=", ">", "<", " CONTAINS ")
        var operator = ""
        for (op in operators) {
            if (condition.contains(op)) {
                operator = op
                break
            }
        }

        if (operator.isEmpty()) {
            return condition.trim().equals("true", ignoreCase = true)
        }

        val parts = condition.split(operator, limit = 2)
        if (parts.size != 2) return false

        val left = parts[0].trim()
        val right = parts[1].trim()

        return when (operator.trim()) {
            "==" -> left == right
            "!=" -> left != right
            ">" -> (left.toDoubleOrNull() ?: 0.0) > (right.toDoubleOrNull() ?: 0.0)
            "<" -> (left.toDoubleOrNull() ?: 0.0) < (right.toDoubleOrNull() ?: 0.0)
            ">=" -> (left.toDoubleOrNull() ?: 0.0) >= (right.toDoubleOrNull() ?: 0.0)
            "<=" -> (left.toDoubleOrNull() ?: 0.0) <= (right.toDoubleOrNull() ?: 0.0)
            "CONTAINS" -> left.contains(right, ignoreCase = true)
            else -> false
        }
    }
}
