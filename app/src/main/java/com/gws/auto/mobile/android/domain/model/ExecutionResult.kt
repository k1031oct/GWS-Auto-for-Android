package com.gws.auto.mobile.android.domain.model

sealed class ExecutionResult(
    val isSuccess: Boolean,
    val outputMessage: String? = null,
    val outputVariables: Map<String, Any> = emptyMap(),
    val updatedParameters: Map<String, String>? = null
) {
    class Success(
        message: String? = null,
        variables: Map<String, Any> = emptyMap(),
        updatedParameters: Map<String, String>? = null
    ) : ExecutionResult(true, message, variables, updatedParameters)

    class Error(
        message: String? = null,
        variables: Map<String, Any> = emptyMap(),
        updatedParameters: Map<String, String>? = null
    ) : ExecutionResult(false, message, variables, updatedParameters)
}
