package com.gws.auto.mobile.android.domain.engine

import com.gws.auto.mobile.android.domain.model.Module

/**
 * Holds the current state of a workflow execution, including variables.
 * This context is passed to each ModuleExecutor.
 */
data class ExecutionContext(
    val module: Module,
    private val variables: MutableMap<String, Any> = mutableMapOf(),
    val workflowEngine: WorkflowEngine? = null,
    private val states: Map<String, String> = emptyMap()
) {
    var nextModuleId: String? = null
        private set

    fun getVariable(name: String): Any? = variables[name]

    fun getState(key: String): String? = states[key]

    fun setVariable(name: String, value: Any) {
        variables[name] = value
    }

    fun setNextModuleId(moduleId: String) {
        nextModuleId = moduleId
    }

    fun resolveVariables(text: String): String {
        var resolvedText = text
        variables.forEach { (key, value) ->
            resolvedText = resolvedText.replace("{{$key}}", value.toString())
        }
        return resolvedText
    }
}
