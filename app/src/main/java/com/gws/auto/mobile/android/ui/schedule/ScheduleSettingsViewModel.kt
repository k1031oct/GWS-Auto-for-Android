package com.gws.auto.mobile.android.ui.schedule

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.gws.auto.mobile.android.data.repository.ScheduleRepository
import com.gws.auto.mobile.android.data.repository.SettingsRepository
import com.gws.auto.mobile.android.data.repository.WorkflowRepository
import com.gws.auto.mobile.android.domain.model.Schedule
import com.gws.auto.mobile.android.domain.model.ScheduleType
import com.gws.auto.mobile.android.domain.model.Workflow
import com.gws.auto.mobile.android.domain.service.NextExecutionTimeCalculator
import com.gws.auto.mobile.android.domain.service.ScheduleWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalTime
import java.time.ZonedDateTime
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class ScheduleSettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context, // Injected context
    private val scheduleRepository: ScheduleRepository,
    private val settingsRepository: SettingsRepository,
    private val workflowRepository: WorkflowRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScheduleSettingsUiState())
    val uiState: StateFlow<ScheduleSettingsUiState> = _uiState.asStateFlow()

    private val _workflows = MutableStateFlow<List<Workflow>>(emptyList())
    val workflows: StateFlow<List<Workflow>> = _workflows.asStateFlow()

    private val scheduleId: String? = savedStateHandle.get("scheduleId")

    init {
        viewModelScope.launch {
            val firstDay = settingsRepository.firstDayOfWeek.first()
            _uiState.update { it.copy(firstDayOfWeek = firstDay) }
        }
        workflowRepository.getAllWorkflows().onEach { workflows ->
            _workflows.value = workflows
            // If not editing, select the first workflow by default
            if (scheduleId == null && workflows.isNotEmpty() && _uiState.value.selectedWorkflowId.isEmpty()) {
                _uiState.update { state -> state.copy(selectedWorkflowId = workflows.first().id) }
            }
        }.launchIn(viewModelScope)

        if (scheduleId != null) {
            loadScheduleForEditing(scheduleId)
        }
    }

    private fun loadScheduleForEditing(id: String) {
        viewModelScope.launch {
            scheduleRepository.getScheduleById(id)?.let { schedule ->
                _uiState.update {
                    it.copy(
                        scheduleType = when (schedule.scheduleType) {
                            ScheduleType.HOURLY -> "時間毎"
                            ScheduleType.DAILY -> "日毎"
                            ScheduleType.WEEKLY -> "週毎"
                            ScheduleType.MONTHLY -> "月毎"
                            ScheduleType.YEARLY -> "年毎"
                        },
                        hourlyInterval = schedule.hourlyInterval ?: 1,
                        dailyTime = schedule.time?.let { LocalTime.parse(it) } ?: LocalTime.of(9, 0),
                        weeklyDays = schedule.weeklyDays?.toSet() ?: emptySet(),
                        weeklyTime = schedule.time?.let { LocalTime.parse(it) } ?: LocalTime.of(9, 0),
                        monthlyDays = schedule.monthlyDays?.toSet() ?: emptySet(),
                        monthlyTime = schedule.time?.let { LocalTime.parse(it) } ?: LocalTime.of(9, 0),
                        yearlyMonth = schedule.yearlyMonth ?: 1,
                        yearlyDayOfMonth = schedule.yearlyDayOfMonth ?: 1,
                        yearlyTime = schedule.time?.let { LocalTime.parse(it) } ?: LocalTime.of(9, 0),
                        selectedWorkflowId = schedule.workflowId
                    )
                }
            }
        }
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
            val selectedWorkflow = _workflows.value.find { it.id == state.selectedWorkflowId }
            if (selectedWorkflow == null) {
                // Handle error: workflow not found
                return@launch
            }
            val schedule = Schedule(
                id = scheduleId ?: UUID.randomUUID().toString(), // Use existing ID if editing
                workflowId = state.selectedWorkflowId,
                workflowName = selectedWorkflow.name,
                scheduleType = when(state.scheduleType) {
                    "時間毎" -> ScheduleType.HOURLY
                    "日毎" -> ScheduleType.DAILY
                    "週毎" -> ScheduleType.WEEKLY
                    "月毎" -> ScheduleType.MONTHLY
                    "年毎" -> ScheduleType.YEARLY
                    else -> throw IllegalArgumentException("Invalid schedule type")
                },
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
            scheduleRepository.createSchedule(schedule) // createSchedule in repo handles both create and update
            scheduleWork(schedule)
        }
    }

    private fun scheduleWork(schedule: Schedule) {
        val workManager = WorkManager.getInstance(context)
        val nextExecutionTime = NextExecutionTimeCalculator.calculateNextExecutionTime(schedule)
        val delay = Duration.between(ZonedDateTime.now(), nextExecutionTime).toMillis()

        val workRequest = OneTimeWorkRequestBuilder<ScheduleWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(Data.Builder().putString(ScheduleWorker.KEY_SCHEDULE_ID, schedule.id).build())
            .build()

        workManager.enqueueUniqueWork(
            schedule.id, // Unique work name
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
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
