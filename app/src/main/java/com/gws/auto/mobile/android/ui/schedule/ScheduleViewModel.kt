package com.gws.auto.mobile.android.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gws.auto.mobile.android.data.repository.ScheduleRepository
import com.gws.auto.mobile.android.data.repository.SettingsRepository
import com.gws.auto.mobile.android.domain.model.Holiday
import com.gws.auto.mobile.android.domain.model.Schedule
import com.gws.auto.mobile.android.domain.service.GoogleApiAuthorizer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    settingsRepository: SettingsRepository,
    private val googleApiAuthorizer: GoogleApiAuthorizer
) : ViewModel() {

    private val _currentDate = MutableStateFlow(LocalDate.now())
    val currentDate: StateFlow<LocalDate> = _currentDate

    // Expose all schedules for the calendar month view
    val allSchedules: StateFlow<List<Schedule>> = scheduleRepository.getSchedulesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Expose schedules filtered by the selected date for the timeline view
    val schedulesForSelectedDate: StateFlow<List<Schedule>> = combine(_currentDate, allSchedules) { date, schedules ->
        schedules.filter { schedule ->
            when (schedule.scheduleType) {
                "daily" -> true
                "weekly" -> schedule.weeklyDays?.any { it.equals(date.dayOfWeek.name, ignoreCase = true) } == true
                "monthly" -> schedule.monthlyDays?.contains(date.dayOfMonth) == true
                "yearly" -> schedule.yearlyMonth == date.monthValue && schedule.yearlyDayOfMonth == date.dayOfMonth
                else -> false
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _holidays = MutableStateFlow<List<Holiday>>(emptyList())
    val holidays: StateFlow<List<Holiday>> = _holidays

    val firstDayOfWeek = settingsRepository.firstDayOfWeek
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Sunday")

    private val holidayCountry = settingsRepository.holidayCountry
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "US")

    init {
        // React to changes in country or the current date
        combine(holidayCountry, currentDate) { _, _ ->
            Unit // We don't need the result, just the trigger.
        }.onEach {
            loadHolidaysForCurrentMonth()
        }.launchIn(viewModelScope)
    }

    fun setCurrentDate(date: LocalDate) {
        _currentDate.value = date
    }

    fun moveToNextMonth() {
        _currentDate.value = _currentDate.value.plusMonths(1)
    }

    fun moveToPreviousMonth() {
        _currentDate.value = _currentDate.value.minusMonths(1)
    }

    fun loadHolidaysForCurrentMonth() {
        if (!googleApiAuthorizer.isSignedIn()) {
            _holidays.value = emptyList() // Clear holidays if not signed in
            return
        }

        viewModelScope.launch {
            val yearMonth = YearMonth.from(_currentDate.value)
            val currentCountry = holidayCountry.value
            _holidays.value = scheduleRepository.getHolidays(currentCountry, yearMonth.year, yearMonth.monthValue)
        }
    }
}
