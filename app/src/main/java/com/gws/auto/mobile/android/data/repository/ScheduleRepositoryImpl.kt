package com.gws.auto.mobile.android.data.repository

import com.gws.auto.mobile.android.data.local.db.WorkflowDao
import com.gws.auto.mobile.android.domain.model.Workflow
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ScheduleRepositoryImpl @Inject constructor(
    private val workflowDao: WorkflowDao
) : ScheduleRepository {

    override fun getScheduledWorkflows(): Flow<List<Workflow>> {
        return workflowDao.getScheduledWorkflows()
    }
}
