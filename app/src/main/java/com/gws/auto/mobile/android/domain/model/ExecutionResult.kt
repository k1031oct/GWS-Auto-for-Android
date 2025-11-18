package com.gws.auto.mobile.android.domain.model

data class ExecutionResult(
    val isSuccess: Boolean,
    val outputMessage: String? = null,
    val outputVariables: Map<String, Any> = emptyMap()
)
