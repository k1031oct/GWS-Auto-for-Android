package com.gws.auto.mobile.android.domain.model

sealed class ExecutionResult(
    val isSuccess: Boolean,
    val outputMessage: String? = null,
    val outputVariables: Map<String, Any> = emptyMap(),
    val updatedStates: Map<String, String>? = null
) {
    class Success(
        message: String? = null,
        variables: Map<String, Any> = emptyMap(),
        updatedStates: Map<String, String>? = null
    ) : ExecutionResult(true, message, variables, updatedStates)

    class Error(
        message: String? = null,
        variables: Map<String, Any> = emptyMap(),
        updatedStates: Map<String, String>? = null
    ) : ExecutionResult(false, message, variables, updatedStates)
}
