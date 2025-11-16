package com.gws.auto.mobile.android.data.repository

import com.gws.auto.mobile.android.domain.model.Holiday
import com.gws.auto.mobile.android.domain.model.Schedule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class ScheduleRepositoryImpl @Inject constructor() : ScheduleRepository {

    override fun getSchedulesFlow(): Flow<List<Schedule>> {
        // Return an empty list to avoid any compilation issues for now.
        return flowOf(emptyList())
    }

    override suspend fun addSchedule(schedule: Schedule) {
        // TODO: Implement
    }

    override suspend fun updateSchedule(schedule: Schedule) {
        // TODO: Implement
    }

    override suspend fun getHolidays(country: String, year: Int, month: Int): List<Holiday> {
        // TODO: Implement
        return emptyList()
    }
}
