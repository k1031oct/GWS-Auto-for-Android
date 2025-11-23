package com.gws.auto.mobile.android.domain.model

sealed class WorkflowListItem {
    data class WorkflowItem(val workflow: Workflow, val isIndented: Boolean = false) : WorkflowListItem()
    data class FolderItem(val folder: WorkflowFolder, val isExpanded: Boolean = false) : WorkflowListItem()
    data class EmptyFolderItem(val folderId: String) : WorkflowListItem()
    object AddItem : WorkflowListItem()
}
