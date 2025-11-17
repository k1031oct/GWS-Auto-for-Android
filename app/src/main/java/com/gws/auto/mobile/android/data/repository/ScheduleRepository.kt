package com.gws.auto.mobile.android.data.repository

import com.gws.auto.mobile.android.domain.model.Holiday
import com.gws.auto.mobile.android.domain.model.Schedule
import kotlinx.coroutines.flow.Flow

interface ScheduleRepository {
    fun getSchedulesFlow(): Flow<List<Schedule>>
    suspend fun createSchedule(schedule: Schedule)
    suspend fun deleteSchedule(scheduleId: String)
    suspend fun getHolidays(countryCode: String, year: Int, month: Int): List<Holiday>
}
