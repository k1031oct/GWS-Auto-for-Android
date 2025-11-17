package com.gws.auto.mobile.android.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gws.auto.mobile.android.data.repository.ScheduleRepository
import com.gws.auto.mobile.android.data.repository.SettingsRepository
import com.gws.auto.mobile.android.domain.model.Holiday
import com.gws.auto.mobile.android.domain.model.Schedule
import com.gws.auto.mobile.android.domain.model.ScheduleType
import com.gws.auto.mobile.android.domain.service.GoogleApiAuthorizer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

// Helper function to safely map Japanese day string to DayOfWeek enum
private fun mapJapaneseDayToDayOfWeek(japaneseDay: String): DayOfWeek? {
    return when (japaneseDay) {
        "月" -> DayOfWeek.MONDAY
        "火" -> DayOfWeek.TUESDAY
        "水" -> DayOfWeek.WEDNESDAY
        "木" -> DayOfWeek.THURSDAY
        "金" -> DayOfWeek.FRIDAY
        "土" -> DayOfWeek.SATURDAY
        "日" -> DayOfWeek.SUNDAY
        else -> null
    }
}

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    settingsRepository: SettingsRepository,
    private val googleApiAuthorizer: GoogleApiAuthorizer
) : ViewModel() {

    private val _currentDate = MutableStateFlow(LocalDate.now())
    val currentDate: StateFlow<LocalDate> = _currentDate

    val allSchedules: StateFlow<List<Schedule>> = scheduleRepository.getSchedulesFlow()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val schedulesForSelectedDate: StateFlow<List<Schedule>> = combine(_currentDate, allSchedules) { date, schedules ->
        schedules.filter { schedule ->
            when (schedule.scheduleType) {
                ScheduleType.HOURLY -> true
                ScheduleType.DAILY -> true
                ScheduleType.WEEKLY -> {
                    val scheduleDays = schedule.weeklyDays?.mapNotNull { mapJapaneseDayToDayOfWeek(it) }
                    scheduleDays?.contains(date.dayOfWeek) == true
                }
                ScheduleType.MONTHLY -> schedule.monthlyDays?.contains(date.dayOfMonth) == true
                ScheduleType.YEARLY -> schedule.yearlyMonth == date.monthValue && schedule.yearlyDayOfMonth == date.dayOfMonth
                else -> false
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _holidays = MutableStateFlow<List<Holiday>>(emptyList())
    val holidays: StateFlow<List<Holiday>> = _holidays

    val firstDayOfWeek = settingsRepository.firstDayOfWeek
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Sunday")

    private val holidayCountry = settingsRepository.holidayCountry
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "US")

    // This flow emits a new value only when the month or year of the current date changes.
    private val currentYearMonth = _currentDate.map { YearMonth.from(it) }.distinctUntilChanged()

    init {
        // Relaunch the holiday loading coroutine whenever the country or the month changes.
        combine(holidayCountry, currentYearMonth) { country, yearMonth ->
            country to yearMonth
        }.onEach { (country, yearMonth) ->
            loadHolidaysForMonth(yearMonth, country)
        }.launchIn(viewModelScope)
    }

    fun setCurrentDate(date: LocalDate) {
        _currentDate.value = date
    }

    fun createSchedule(schedule: Schedule) {
        viewModelScope.launch {
            scheduleRepository.createSchedule(schedule)
        }
    }

    fun deleteSchedule(scheduleId: String) {
        viewModelScope.launch {
            scheduleRepository.deleteSchedule(scheduleId)
        }
    }

    fun moveToNextMonth() {
        _currentDate.value = _currentDate.value.plusMonths(1)
    }

    fun moveToPreviousMonth() {
        _currentDate.value = _currentDate.value.minusMonths(1)
    }

    private suspend fun loadHolidaysForMonth(yearMonth: YearMonth, country: String) {
        if (!googleApiAuthorizer.isSignedIn()) {
            Timber.d("Not signed in, clearing holidays.")
            _holidays.value = emptyList()
            return
        }

        Timber.d("Loading holidays for $yearMonth, country: $country")
        try {
            // The actual repository call is on a background thread (Dispatchers.IO)
            val holidayList = scheduleRepository.getHolidays(country, yearMonth.year, yearMonth.monthValue)
            // Updating the state flow on the main thread
            _holidays.value = holidayList
            Timber.d("Loaded ${holidayList.size} holidays.")
        } catch (e: Exception) {
            Timber.e(e, "Failed to load holidays.")
            _holidays.value = emptyList()
        }
    }
}
