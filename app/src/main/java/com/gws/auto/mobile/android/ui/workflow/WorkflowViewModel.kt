package com.gws.auto.mobile.android.ui.workflow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gws.auto.mobile.android.data.repository.SearchHistoryRepository
import com.gws.auto.mobile.android.data.repository.WorkflowFolderRepository
import com.gws.auto.mobile.android.data.repository.WorkflowRepository
import com.gws.auto.mobile.android.domain.model.Workflow
import com.gws.auto.mobile.android.domain.model.WorkflowFolder
import com.gws.auto.mobile.android.domain.model.WorkflowListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class WorkflowViewModel @Inject constructor(
    private val workflowRepository: WorkflowRepository,
    private val workflowFolderRepository: WorkflowFolderRepository,
    private val searchHistoryRepository: SearchHistoryRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _isFavoriteFilterActive = MutableStateFlow(false)
    private val _expandedFolderIds = MutableStateFlow<Set<String>>(emptySet())

    val isFavoriteFilterActive: StateFlow<Boolean> = _isFavoriteFilterActive

    val filteredItems: StateFlow<List<WorkflowListItem>> = combine(
        workflowRepository.getAllWorkflows(),
        workflowFolderRepository.getAllWorkflowFolders(),
        _searchQuery,
        _isFavoriteFilterActive,
        _expandedFolderIds
    ) { workflows, folders, query, isFavoriteFilterActive, expandedIds ->

        val workflowMap = workflows.associateBy { it.id }
        val allWorkflowIdsInFolders = folders.flatMap { it.workflowIds }.toSet()

        val isWorkflowMatch: (Workflow) -> Boolean = { workflow ->
            val matchesQuery = query.isBlank() ||
                    workflow.name.contains(query, ignoreCase = true) ||
                    workflow.tags.any { it.contains(query, ignoreCase = true) }
            val matchesFavorite = !isFavoriteFilterActive || workflow.isFavorite
            matchesQuery && matchesFavorite
        }

        val topLevelWorkflows = workflows.filter { it.id !in allWorkflowIdsInFolders }
        val filteredTopLevel = topLevelWorkflows.filter(isWorkflowMatch)

        val result = mutableListOf<WorkflowListItem>()
        
        folders.forEach { folder ->
            val workflowsInFolder = folder.workflowIds.mapNotNull { workflowMap[it] }
            val matchingWorkflows = workflowsInFolder.filter(isWorkflowMatch)
            val folderMatches = query.isBlank() || folder.name.contains(query, ignoreCase = true)

            if (folderMatches || matchingWorkflows.isNotEmpty()) {
                // Auto-expand if searching and matches found inside, otherwise respect user expansion
                val isExpanded = if (query.isNotBlank()) true else folder.id in expandedIds
                
                result.add(WorkflowListItem.FolderItem(folder, isExpanded))
                if (isExpanded) {
                    // If folder matches by name, show all workflows in it (respecting favorite filter)
                    // Otherwise show only matching workflows
                    val itemsToShow = if (folderMatches && query.isNotBlank()) {
                         workflowsInFolder.filter { !isFavoriteFilterActive || it.isFavorite }
                    } else {
                        matchingWorkflows
                    }
                    result.addAll(itemsToShow.map { WorkflowListItem.WorkflowItem(it, isIndented = true) })
                    
                    if (itemsToShow.isEmpty()) {
                        result.add(WorkflowListItem.EmptyFolderItem(folder.id))
                    }
                }
            }
        }

        result.addAll(filteredTopLevel.map { WorkflowListItem.WorkflowItem(it) })
        result.add(WorkflowListItem.AddItem)
        result

    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun addSearchHistory(query: String) = viewModelScope.launch {
        searchHistoryRepository.insertSearchHistory(query)
    }

    fun deleteWorkflow(workflow: Workflow) = viewModelScope.launch {
        workflowRepository.deleteWorkflow(workflow)
    }

    fun deleteWorkflowFolder(folderId: String) = viewModelScope.launch {
        workflowFolderRepository.deleteWorkflowFolder(folderId)
    }

    fun toggleFavorite(workflow: Workflow) = viewModelScope.launch {
        val updatedWorkflow = workflow.copy(isFavorite = !workflow.isFavorite)
        workflowRepository.saveWorkflow(updatedWorkflow)
    }

    fun toggleFavoriteFilter() {
        _isFavoriteFilterActive.value = !_isFavoriteFilterActive.value
    }

    fun createFolder(name: String) = viewModelScope.launch {
        val newFolder = WorkflowFolder(
            id = UUID.randomUUID().toString(),
            name = name,
            workflowIds = emptyList()
        )
        workflowFolderRepository.insertWorkflowFolder(newFolder)
    }

    fun toggleFolderExpansion(folderId: String) {
        val currentExpanded = _expandedFolderIds.value.toMutableSet()
        if (folderId in currentExpanded) {
            currentExpanded.remove(folderId)
        } else {
            currentExpanded.add(folderId)
        }
        _expandedFolderIds.value = currentExpanded
    }

    fun moveWorkflowToFolder(workflowId: String, folderId: String) = viewModelScope.launch {
        val allFolders = workflowFolderRepository.getAllWorkflowFolders().first()
        val sourceFolder = allFolders.find { it.workflowIds.contains(workflowId) }

        // If folderId is empty, remove from current folder (move to root)
        if (folderId.isEmpty()) {
            if (sourceFolder != null) {
                val updatedSourceIds = sourceFolder.workflowIds.toMutableList().also { it.remove(workflowId) }
                workflowFolderRepository.updateWorkflowFolder(sourceFolder.copy(workflowIds = updatedSourceIds))
            }
            return@launch
        }

        // Otherwise, existing logic to move between folders
        val targetFolder = allFolders.find { it.id == folderId }

        // If target folder doesn't exist, or it's the same as the source, do nothing.
        if (targetFolder == null || sourceFolder?.id == targetFolder.id) {
            return@launch
        }

        // Remove from source folder
        if (sourceFolder != null) {
            val updatedSourceIds = sourceFolder.workflowIds.toMutableList().also { it.remove(workflowId) }
            workflowFolderRepository.updateWorkflowFolder(sourceFolder.copy(workflowIds = updatedSourceIds))
        }

        // Add to target folder
        val updatedTargetIds = targetFolder.workflowIds.toMutableList().also { it.add(workflowId) }
        workflowFolderRepository.updateWorkflowFolder(targetFolder.copy(workflowIds = updatedTargetIds))
    }

    fun reorderWorkflows(fromId: String, toId: String) = viewModelScope.launch {
        val allWorkflows = workflowRepository.getAllWorkflows().first()
        val allFolders = workflowFolderRepository.getAllWorkflowFolders().first()

        val fromWorkflow = allWorkflows.find { it.id == fromId } ?: return@launch
        val toWorkflow = allWorkflows.find { it.id == toId } ?: return@launch

        // Check if both are root workflows
        val isFromRoot = allFolders.none { it.workflowIds.contains(fromId) }
        val isToRoot = allFolders.none { it.workflowIds.contains(toId) }

        if (isFromRoot && isToRoot) {
            val rootWorkflows = allWorkflows.filter { workflow ->
                allFolders.none { it.workflowIds.contains(workflow.id) }
            }.sortedBy { it.order }.toMutableList()

            val fromIndex = rootWorkflows.indexOfFirst { it.id == fromId }
            val toIndex = rootWorkflows.indexOfFirst { it.id == toId }

            if (fromIndex != -1 && toIndex != -1) {
                val item = rootWorkflows.removeAt(fromIndex)
                rootWorkflows.add(toIndex, item)

                val updatedWorkflows = rootWorkflows.mapIndexed { index, workflow ->
                    workflow.copy(order = index)
                }
                workflowRepository.updateWorkflowOrders(updatedWorkflows)
            }
        } else {
            // Handle reordering within the same folder
            val fromFolder = allFolders.find { it.workflowIds.contains(fromId) }
            val toFolder = allFolders.find { it.workflowIds.contains(toId) }

            if (fromFolder != null && fromFolder.id == toFolder?.id) {
                val workflowIds = fromFolder.workflowIds.toMutableList()
                val fromIndex = workflowIds.indexOf(fromId)
                val toIndex = workflowIds.indexOf(toId)

                if (fromIndex != -1 && toIndex != -1) {
                    val item = workflowIds.removeAt(fromIndex)
                    workflowIds.add(toIndex, item)
                    workflowFolderRepository.updateWorkflowFolder(fromFolder.copy(workflowIds = workflowIds))
                }
            }
        }
    }
}
