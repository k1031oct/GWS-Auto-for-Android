package com.gws.auto.mobile.android.ui.workflow.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gws.auto.mobile.android.data.repository.TagRepository
import com.gws.auto.mobile.android.data.repository.WorkflowRepository
import com.gws.auto.mobile.android.domain.engine.WorkflowEngine
import com.gws.auto.mobile.android.domain.model.ExecutionResult
import com.gws.auto.mobile.android.domain.model.Module
import com.gws.auto.mobile.android.domain.model.Tag
import com.gws.auto.mobile.android.domain.model.Workflow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class WorkflowEditorViewModel @Inject constructor(
    private val workflowRepository: WorkflowRepository,
    private val tagRepository: TagRepository,
    private val workflowEngine: WorkflowEngine
) : ViewModel() {

    private val _workflow = MutableStateFlow<Workflow?>(null)
    val workflow: StateFlow<Workflow?> = _workflow.asStateFlow()

    private var loadedWorkflowId: String? = null

    private val _modules = MutableStateFlow<List<Module>>(emptyList())
    val modules: StateFlow<List<Module>> = _modules.asStateFlow()

    private val _singleModuleExecutionResult = MutableStateFlow<ExecutionResult?>(null)
    val singleModuleExecutionResult: StateFlow<ExecutionResult?> = _singleModuleExecutionResult.asStateFlow()

    val availableTags: StateFlow<List<Tag>> = tagRepository.getAllTags()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedTags = MutableStateFlow<List<String>>(emptyList())
    val selectedTags: StateFlow<List<String>> = _selectedTags.asStateFlow()

    fun loadWorkflow(workflowId: String) {
        loadedWorkflowId = workflowId
        viewModelScope.launch {
            val workflow = workflowRepository.getWorkflowById(workflowId)
            if (workflow == null) {
                Timber.w("Workflow not found for id: $workflowId")
            } else {
                Timber.d("Workflow loaded: ${workflow.name} ($workflowId)")
            }
            _workflow.value = workflow
            workflow?.let {
                _modules.value = it.modules
                _selectedTags.value = it.tags
            }
        }
    }

    fun addModule(module: Module) {
        _modules.value = _modules.value + module
    }

    fun removeModule(module: Module) {
        _modules.value = _modules.value.filter { it.id != module.id }
    }

    fun updateModuleParameters(moduleId: String, parameters: Map<String, String>) {
        _modules.update {
            it.map {
                if (it.id == moduleId) {
                    it.copy(parameters = parameters)
                } else {
                    it
                }
            }
        }
    }
    
    fun setModuleEnabled(moduleId: String, isEnabled: Boolean) {
        _modules.update {
            it.map {
                if (it.id == moduleId) {
                    it.copy(isEnabled = isEnabled)
                } else {
                    it
                }
            }
        }
    }
    
    fun runSingleModule(module: Module) {
        viewModelScope.launch {
            _singleModuleExecutionResult.value = workflowEngine.executeSingleModule(module)
        }
    }
    
    fun clearSingleModuleExecutionResult() {
        _singleModuleExecutionResult.value = null
    }

    fun addTagToWorkflow(tagName: String) {
        if (!_selectedTags.value.contains(tagName)) {
            _selectedTags.value = _selectedTags.value + tagName
        }
        // Also ensure the tag exists in the repository
        viewModelScope.launch {
            tagRepository.addTag(Tag(name = tagName))
        }
    }

    fun removeTagFromWorkflow(tagName: String) {
        _selectedTags.value = _selectedTags.value - tagName
    }

    fun insertModule(module: Module, index: Int) {
        val currentList = _modules.value.toMutableList()
        if (index >= 0 && index <= currentList.size) {
            currentList.add(index, module)
            _modules.value = currentList
        }
    }

    fun reorderModules(newModules: List<Module>) {
        _modules.value = newModules
    }

    suspend fun saveWorkflow(name: String, description: String) {
        val currentWorkflow = _workflow.value
        val workflow = if (currentWorkflow != null) {
            currentWorkflow.copy(
                name = name,
                description = description,
                modules = _modules.value,
                tags = _selectedTags.value
            )
        } else {
            Timber.w("Current workflow is null, creating new one. loadedWorkflowId: $loadedWorkflowId")
            Workflow(
                id = loadedWorkflowId ?: UUID.randomUUID().toString(),
                name = name,
                description = description,
                modules = _modules.value,
                tags = _selectedTags.value
            )
        }
        workflowRepository.saveWorkflow(workflow)
        Timber.d("Workflow saved: ${workflow.name} (${workflow.id})")
    }
}
