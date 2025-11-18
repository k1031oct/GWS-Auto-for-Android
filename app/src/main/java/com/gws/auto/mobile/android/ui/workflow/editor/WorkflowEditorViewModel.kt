package com.gws.auto.mobile.android.ui.workflow.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gws.auto.mobile.android.data.repository.WorkflowRepository
import com.gws.auto.mobile.android.domain.engine.WorkflowEngine
import com.gws.auto.mobile.android.domain.model.ExecutionResult
import com.gws.auto.mobile.android.domain.model.Module
import com.gws.auto.mobile.android.domain.model.Workflow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class WorkflowEditorViewModel @Inject constructor(
    private val workflowRepository: WorkflowRepository,
    private val workflowEngine: WorkflowEngine
) : ViewModel() {

    private val _workflow = MutableStateFlow<Workflow?>(null)
    val workflow: StateFlow<Workflow?> = _workflow.asStateFlow()

    private val _modules = MutableStateFlow<List<Module>>(emptyList())
    val modules: StateFlow<List<Module>> = _modules.asStateFlow()

    private val _singleModuleExecutionResult = MutableStateFlow<ExecutionResult?>(null)
    val singleModuleExecutionResult: StateFlow<ExecutionResult?> = _singleModuleExecutionResult.asStateFlow()

    fun loadWorkflow(workflowId: String) {
        viewModelScope.launch {
            val workflow = workflowRepository.getWorkflowById(workflowId)
            _workflow.value = workflow
            workflow?.let {
                _modules.value = it.modules
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

    suspend fun saveWorkflow(name: String, description: String) {
        val currentWorkflow = _workflow.value
        val workflow = if (currentWorkflow != null) {
            currentWorkflow.copy(
                name = name,
                description = description,
                modules = _modules.value
            )
        } else {
            Workflow(
                id = UUID.randomUUID().toString(),
                name = name,
                description = description,
                modules = _modules.value
            )
        }
        workflowRepository.saveWorkflow(workflow)
    }
}
