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

    val uiState: StateFlow<List<HistoryListItem>> = combine(
        historyRepository.getAllHistory(),
        _expandedIds,
        _isBookmarkFilterActive
    ) { histories, expandedIds, isBookmarkFilterActive ->
        val filteredHistories = if (isBookmarkFilterActive) {
            histories.filter { it.isBookmarked }
        } else {
            histories
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

    fun toggleBookmarkFilter() {
        _isBookmarkFilterActive.value = !_isBookmarkFilterActive.value
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
