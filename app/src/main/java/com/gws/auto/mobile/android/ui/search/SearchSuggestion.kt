package com.gws.auto.mobile.android.ui.search

import com.gws.auto.mobile.android.domain.model.Tag
import com.gws.auto.mobile.android.domain.model.Workflow
import com.gws.auto.mobile.android.domain.model.WorkflowFolder

sealed class SearchSuggestion {
    data class WorkflowItem(val workflow: Workflow) : SearchSuggestion()
    data class FolderItem(val folder: WorkflowFolder) : SearchSuggestion()
    data class TagItem(val tag: Tag) : SearchSuggestion()
}
