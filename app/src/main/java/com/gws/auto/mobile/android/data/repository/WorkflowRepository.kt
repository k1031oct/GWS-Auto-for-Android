package com.gws.auto.mobile.android.data.repository

import com.gws.auto.mobile.android.data.local.db.WorkflowDao
import com.gws.auto.mobile.android.domain.model.Workflow
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkflowRepository @Inject constructor(
    private val workflowDao: WorkflowDao
) {

    fun getAllWorkflows(): Flow<List<Workflow>> {
        return workflowDao.getAllWorkflows()
    }

    suspend fun saveWorkflow(workflow: Workflow) {
        workflowDao.insertWorkflow(workflow)
    }

    suspend fun deleteWorkflow(workflow: Workflow) {
        workflowDao.deleteWorkflow(workflow)
    }

    suspend fun getWorkflowById(id: String): Workflow? {
        return workflowDao.getWorkflowById(id)
    }

    suspend fun updateWorkflowOrders(workflows: List<Workflow>) {
        workflowDao.updateWorkflows(workflows)
    }
}
