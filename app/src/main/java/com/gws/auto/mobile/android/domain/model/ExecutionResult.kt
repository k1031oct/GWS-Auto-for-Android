package com.gws.auto.mobile.android.domain.model

sealed class ExecutionResult(
    val isSuccess: Boolean,
    val outputMessage: String? = null,
    val outputVariables: Map<String, Any> = emptyMap()
) {
    class Success(
        message: String? = null,
        variables: Map<String, Any> = emptyMap()
    ) : ExecutionResult(true, message, variables)

    class Error(
        message: String? = null,
        variables: Map<String, Any> = emptyMap()
    ) : ExecutionResult(false, message, variables)
}
