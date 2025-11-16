package com.gws.auto.mobile.android.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gws.auto.mobile.android.data.repository.ScheduleRepository
import com.gws.auto.mobile.android.data.repository.SettingsRepository
import com.gws.auto.mobile.android.data.repository.WorkflowRepository
import com.gws.auto.mobile.android.domain.model.Schedule
import com.gws.auto.mobile.android.domain.model.Workflow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ScheduleSettingsViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    private val settingsRepository: SettingsRepository,
    private val workflowRepository: WorkflowRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScheduleSettingsUiState())
    val uiState: StateFlow<ScheduleSettingsUiState> = _uiState.asStateFlow()

    private val _workflows = MutableStateFlow<List<Workflow>>(emptyList())
    val workflows: StateFlow<List<Workflow>> = _workflows.asStateFlow()

    init {
        viewModelScope.launch {
            val firstDay = settingsRepository.firstDayOfWeek.first()
            _uiState.update { it.copy(firstDayOfWeek = firstDay) }
        }
        
        // Start observing real workflows
        workflowRepository.getAllWorkflows().onEach { realWorkflows ->
            val dummyWorkflows = createDummyWorkflows()
            val allWorkflows = dummyWorkflows + realWorkflows
            _workflows.value = allWorkflows
            if (allWorkflows.isNotEmpty() && _uiState.value.selectedWorkflowId.isEmpty()) {
                _uiState.update { state -> state.copy(selectedWorkflowId = allWorkflows.first().id) }
            }
        }.launchIn(viewModelScope)
    }

    private fun createDummyWorkflows(): List<Workflow> {
        return listOf(
            Workflow(id = "dummy-1", name = "Dummy Workflow 1", modules = emptyList()),
            Workflow(id = "dummy-2", name = "Dummy Workflow 2", modules = emptyList())
        )
    }

    fun onScheduleTypeChange(type: String) {
        _uiState.update { it.copy(scheduleType = type) }
    }

    fun setHourlyInterval(interval: Int) {
        _uiState.update { it.copy(hourlyInterval = interval) }
    }

    fun setDailyTime(time: LocalTime) {
        _uiState.update { it.copy(dailyTime = time) }
    }

    fun toggleWeeklyDay(day: String) {
        val currentDays = _uiState.value.weeklyDays
        val newDays = if (day in currentDays) currentDays - day else currentDays + day
        _uiState.update { it.copy(weeklyDays = newDays) }
    }

    fun setWeeklyTime(time: LocalTime) {
        _uiState.update { it.copy(weeklyTime = time) }
    }

    fun toggleMonthlyDay(day: Int) {
        val currentDays = _uiState.value.monthlyDays
        val newDays = if (day in currentDays) currentDays - day else currentDays + day
        _uiState.update { it.copy(monthlyDays = newDays) }
    }

    fun setMonthlyTime(time: LocalTime) {
        _uiState.update { it.copy(monthlyTime = time) }
    }

    fun setYearlyMonth(month: Int) {
        _uiState.update { it.copy(yearlyMonth = month) }
    }

    fun setYearlyDayOfMonth(day: Int) {
        _uiState.update { it.copy(yearlyDayOfMonth = day) }
    }

    fun setYearlyTime(time: LocalTime) {
        _uiState.update { it.copy(yearlyTime = time) }
    }
    
    fun onWorkflowSelected(workflowId: String) {
        _uiState.update { it.copy(selectedWorkflowId = workflowId) }
    }

    fun saveSchedule() {
        viewModelScope.launch {
            val state = _uiState.value
            val schedule = Schedule(
                id = UUID.randomUUID().toString(),
                workflowId = state.selectedWorkflowId,
                scheduleType = state.scheduleType,
                hourlyInterval = if (state.scheduleType == "時間毎") state.hourlyInterval else null,
                time = when(state.scheduleType) {
                    "日毎" -> state.dailyTime.toString()
                    "週毎" -> state.weeklyTime.toString()
                    "月毎" -> state.monthlyTime.toString()
                    "年毎" -> state.yearlyTime.toString()
                    else -> null
                },
                weeklyDays = if (state.scheduleType == "週毎") state.weeklyDays.toList() else null,
                monthlyDays = if (state.scheduleType == "月毎") state.monthlyDays.toList() else null,
                yearlyMonth = if (state.scheduleType == "年毎") state.yearlyMonth else null,
                yearlyDayOfMonth = if (state.scheduleType == "年毎") state.yearlyDayOfMonth else null,
            )
            scheduleRepository.addSchedule(schedule)
        }
    }
}

data class ScheduleSettingsUiState(
    val scheduleType: String = "日毎",
    val hourlyInterval: Int = 1,
    val dailyTime: LocalTime = LocalTime.of(9, 0),
    val weeklyDays: Set<String> = emptySet(),
    val weeklyTime: LocalTime = LocalTime.of(9, 0),
    val monthlyDays: Set<Int> = emptySet(),
    val monthlyTime: LocalTime = LocalTime.of(9, 0),
    val yearlyMonth: Int = 1,
    val yearlyDayOfMonth: Int = 1,
    val yearlyTime: LocalTime = LocalTime.of(9, 0),
    val firstDayOfWeek: String = "Sunday",
    val selectedWorkflowId: String = ""
)
