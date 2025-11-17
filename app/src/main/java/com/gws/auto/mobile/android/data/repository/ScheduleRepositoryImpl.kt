package com.gws.auto.mobile.android.data.repository

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.gws.auto.mobile.android.data.local.db.ScheduleDao
import com.gws.auto.mobile.android.domain.model.Holiday
import com.gws.auto.mobile.android.domain.model.Schedule
import com.gws.auto.mobile.android.domain.service.CalendarApiService
import com.gws.auto.mobile.android.domain.service.GoogleApiAuthorizer
import com.gws.auto.mobile.android.domain.service.NextExecutionTimeCalculator
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

    override suspend fun getScheduleById(scheduleId: String): Schedule? {
        return scheduleDao.getScheduleById(scheduleId)
    }

    override suspend fun createSchedule(schedule: Schedule) {
        scheduleDao.insertSchedule(schedule)
        scheduleWorkflow(schedule)
    }

    override suspend fun deleteSchedule(scheduleId: String) {
        scheduleDao.deleteScheduleById(scheduleId)
        workManager.cancelUniqueWork(scheduleId)
    }

    private fun scheduleWorkflow(schedule: Schedule) {
        val delay = NextExecutionTimeCalculator.calculateDelay(schedule)
        
        val workRequest = OneTimeWorkRequestBuilder<ScheduleWorker>()
            .setInitialDelay(delay.toMillis(), TimeUnit.MILLISECONDS)
            .setInputData(Data.Builder().putString(ScheduleWorker.KEY_SCHEDULE_ID, schedule.id).build())
            .build()

        workManager.enqueueUniqueWork(
            schedule.id, // Use schedule ID as unique work name
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    override suspend fun getHolidays(countryCode: String, year: Int, month: Int): List<Holiday> {
        return calendarApiService.getHolidays(countryCode, year, month)
    }
}
