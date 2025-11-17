package com.gws.auto.mobile.android.data.repository

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.gws.auto.mobile.android.data.local.db.ScheduleDao
import com.gws.auto.mobile.android.domain.model.Holiday
import com.gws.auto.mobile.android.domain.model.Schedule
import com.gws.auto.mobile.android.domain.service.CalendarApiService
import com.gws.auto.mobile.android.domain.service.GoogleApiAuthorizer
import com.gws.auto.mobile.android.domain.service.ScheduleWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ScheduleRepositoryImpl @Inject constructor(
    private val scheduleDao: ScheduleDao,
    @ApplicationContext private val context: Context,
    private val calendarApiService: CalendarApiService,
    private val googleApiAuthorizer: GoogleApiAuthorizer
) : ScheduleRepository {

    private val workManager = WorkManager.getInstance(context)

    override fun getSchedulesFlow(): Flow<List<Schedule>> {
        return scheduleDao.getAllSchedules()
    }

    override suspend fun createSchedule(schedule: Schedule) {
        scheduleDao.insertSchedule(schedule)
        val workRequest = PeriodicWorkRequestBuilder<ScheduleWorker>(
            schedule.hourlyInterval?.toLong() ?: 24, TimeUnit.HOURS
        )
            .setInputData(Data.Builder().putString(ScheduleWorker.KEY_WORKFLOW_ID, schedule.workflowId).build())
            .build()

        workManager.enqueueUniquePeriodicWork(
            schedule.id,
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }

    override suspend fun deleteSchedule(scheduleId: String) {
        scheduleDao.deleteScheduleById(scheduleId)
        workManager.cancelUniqueWork(scheduleId)
    }

    override suspend fun getHolidays(countryCode: String, year: Int, month: Int): List<Holiday> {
        return calendarApiService.getHolidays(countryCode, year, month)
    }
}
