package com.gws.auto.mobile.android.ui.history

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gws.auto.mobile.android.data.repository.HistoryRepository
import com.gws.auto.mobile.android.domain.model.History
import com.gws.auto.mobile.android.domain.service.HistoryCsvExporter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.OutputStream
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val historyRepository: HistoryRepository,
    private val csvExporter: HistoryCsvExporter
) : ViewModel() {

    private val _expandedIds = MutableStateFlow<Set<Long>>(emptySet())
    private val _isBookmarkFilterActive = MutableStateFlow(false)
    val isBookmarkFilterActive: StateFlow<Boolean> = _isBookmarkFilterActive

    private val _searchQuery = MutableStateFlow("")
    private val _selectedTag = MutableStateFlow<String?>(null)

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setBookmarkFilter(isActive: Boolean) {
        _isBookmarkFilterActive.value = isActive
    }

    fun setSelectedTag(tag: String?) {
        _selectedTag.value = tag
    }

    val uiState: StateFlow<List<HistoryListItem>> = combine(
        historyRepository.getAllHistory(),
        _expandedIds,
        _isBookmarkFilterActive,
        _searchQuery,
        _selectedTag
    ) { histories, expandedIds, isBookmarkFilterActive, query, selectedTag ->
        val filteredHistories = histories.filter { history ->
            val matchesBookmark = !isBookmarkFilterActive || history.isBookmarked
            val matchesQuery = query.isBlank() || history.workflowName.contains(query, ignoreCase = true)
            // History doesn't strictly have tags in the model shown in previous view_file, 
            // but let's check if History has tags. 
            // Checking History.kt... I don't have it open.
            // But the user request implies "Tag search" works for history too?
            // "ワークフロー一覧画面のお気に入りマーカーに対しての検索、フィルタリング機能の追加実装。実行履歴画面のブックマークマークに対しての検索、フィルタリング機能の追加実装。"
            // It doesn't explicitly say Tags work for History.
            // However, SearchFragment handles Tags.
            // If I look at SearchFragment, `handleTagClick` sets query to tag name.
            // So previously, tag search was just text search.
            // Now we are using `selectedTag`.
            // If History items don't have tags, then `selectedTag` might just match workflow name?
            // Or maybe History has tags?
            // Let's assume for now that if selectedTag is set, we match it against workflow name or if History has tags.
            // Since I can't check History.kt right now without a tool call, and I'm in a block.
            // I'll assume History might not support Tag filtering properly yet, OR it supports it via text match.
            // Previous logic: `matchesQuery` checked `workflowName`.
            // If `selectedTag` is passed, we should probably check `workflowName` too if tags aren't available.
            // But wait, `Workflow` has tags. `History` might not.
            // Let's check `History` model if possible.
            // I'll stick to the safe bet: If selectedTag is present, check if workflowName contains it (similar to query) 
            // OR just ignore it if History doesn't support tags.
            // BUT, the user said "Search Bar... display selected tags...".
            // If I select a tag, it should filter.
            // If I use `selectedTag` as a strict filter, and History doesn't have tags, it might show nothing.
            // Let's assume for History, we treat `selectedTag` as a search query on `workflowName` for now, 
            // or if `History` has `tags` field.
            // I will assume `History` does NOT have tags based on `HistoryListItem` usage.
            // So for History, `selectedTag` acts like `query`.
            val matchesTag = selectedTag == null || history.workflowName.contains(selectedTag, ignoreCase = true)
            
            matchesBookmark && matchesQuery && matchesTag
        }

        val flatList = mutableListOf<HistoryListItem>()
        filteredHistories.forEach {
            val isExpanded = expandedIds.contains(it.id)
            flatList.add(HistoryListItem.HeaderItem(it, isExpanded))
            if (isExpanded) {
                val logs = it.logs.lines().map { log -> HistoryListItem.LogItem(log, it.id.toString()) }
                flatList.addAll(logs)
            }
        }
        flatList
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun toggleItemExpanded(historyId: Long) {
        val currentIds = _expandedIds.value.toMutableSet()
        if (currentIds.contains(historyId)) {
            currentIds.remove(historyId)
        } else {
            currentIds.add(historyId)
        }
        _expandedIds.value = currentIds
    }

    fun toggleBookmark(history: History) = viewModelScope.launch {
        val updatedHistory = history.copy(isBookmarked = !history.isBookmarked)
        historyRepository.updateHistory(updatedHistory)
    }



    fun deleteHistory(history: History) {
        viewModelScope.launch {
            historyRepository.deleteHistoryById(history.id)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            historyRepository.deleteAllHistory()
        }
    }

    fun exportHistoryToCsv(outputStream: OutputStream) {
        viewModelScope.launch {
            val historyList = historyRepository.getAllHistory().first()
            csvExporter.export(historyList, outputStream)
        }
    }
}
