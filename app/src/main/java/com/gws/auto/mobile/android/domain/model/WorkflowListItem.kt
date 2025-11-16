package com.gws.auto.mobile.android.domain.model

sealed class WorkflowListItem {
    data class WorkflowItem(val workflow: Workflow) : WorkflowListItem()
    data class FolderItem(val folder: WorkflowFolder) : WorkflowListItem()
    object AddItem : WorkflowListItem()
}
