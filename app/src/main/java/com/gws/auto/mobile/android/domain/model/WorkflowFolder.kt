package com.gws.auto.mobile.android.domain.model

data class WorkflowFolder(
    val id: String,
    val name: String,
    val workflowIds: List<String>
)
