package com.gws.auto.mobile.android.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gws.auto.mobile.android.data.repository.SearchHistoryRepository
import com.gws.auto.mobile.android.data.repository.TagRepository
import com.gws.auto.mobile.android.domain.model.SearchHistory
import com.gws.auto.mobile.android.domain.model.Tag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.gws.auto.mobile.android.data.repository.WorkflowFolderRepository
import com.gws.auto.mobile.android.data.repository.WorkflowRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val tagRepository: TagRepository,
    private val searchHistoryRepository: SearchHistoryRepository,
    private val workflowRepository: WorkflowRepository,
    private val workflowFolderRepository: WorkflowFolderRepository
) : ViewModel() {

    val tags: StateFlow<List<Tag>> = tagRepository.getAllTags()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val searchHistory: StateFlow<List<SearchHistory>> = searchHistoryRepository.getSearchHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    val searchSuggestions: StateFlow<List<SearchSuggestion>> = combine(
        _searchQuery,
        workflowRepository.getAllWorkflows(),
        workflowFolderRepository.getAllWorkflowFolders(),
        tagRepository.getAllTags()
    ) { query, workflows, folders, tags ->
        if (query.isBlank()) return@combine emptyList()

        val workflowSuggestions = workflows.filter { it.name.contains(query, ignoreCase = true) }
            .map { SearchSuggestion.WorkflowItem(it) }

        val folderSuggestions = folders.filter { it.name.contains(query, ignoreCase = true) }
            .map { SearchSuggestion.FolderItem(it) }

        val tagSuggestions = tags.filter { it.name.contains(query, ignoreCase = true) }
            .map { SearchSuggestion.TagItem(it) }

        (workflowSuggestions + folderSuggestions + tagSuggestions)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addTag(tagName: String) = viewModelScope.launch {
        tagRepository.addTag(Tag(name = tagName))
    }

    fun deleteTag(tag: Tag) = viewModelScope.launch {
        tagRepository.deleteTag(tag)
    }

    fun addSearchHistory(query: String) = viewModelScope.launch {
        searchHistoryRepository.insertSearchHistory(query)
    }

    fun clearSearchHistory() = viewModelScope.launch {
        searchHistoryRepository.clearSearchHistory()
    }

    fun deleteSearchHistoryItem(query: String) = viewModelScope.launch {
        searchHistoryRepository.deleteSearchHistory(query)
    }
}
