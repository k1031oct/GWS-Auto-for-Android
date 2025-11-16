package com.gws.auto.mobile.android.data.repository

import com.gws.auto.mobile.android.data.local.db.WorkflowFolderDao
import com.gws.auto.mobile.android.domain.model.WorkflowFolder
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WorkflowFolderRepository @Inject constructor(
    private val workflowFolderDao: WorkflowFolderDao
) {

    fun getAllWorkflowFolders(): Flow<List<WorkflowFolder>> {
        return workflowFolderDao.getAllWorkflowFolders()
    }

    suspend fun insertWorkflowFolder(folder: WorkflowFolder) {
        workflowFolderDao.insertWorkflowFolder(folder)
    }

    suspend fun updateWorkflowFolder(folder: WorkflowFolder) {
        workflowFolderDao.updateWorkflowFolder(folder)
    }

    suspend fun deleteWorkflowFolder(folderId: String) {
        workflowFolderDao.deleteWorkflowFolder(folderId)
    }
}
