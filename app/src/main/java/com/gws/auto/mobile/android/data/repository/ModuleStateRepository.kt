package com.gws.auto.mobile.android.data.repository

import com.gws.auto.mobile.android.data.local.db.ModuleStateDao
import com.gws.auto.mobile.android.domain.model.ModuleState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ModuleStateRepository @Inject constructor(
    private val moduleStateDao: ModuleStateDao
) {

    suspend fun getState(workflowId: String, moduleId: String, key: String): String? {
        return moduleStateDao.getState(workflowId, moduleId, key)?.value
    }

    suspend fun saveState(workflowId: String, moduleId: String, key: String, value: String) {
        val state = ModuleState(
            workflowId = workflowId,
            moduleId = moduleId,
            key = key,
            value = value
        )
        moduleStateDao.insertState(state)
    }

    /**
     * Updates the state only if the new timestamp is newer than the existing one.
     * This is useful for parallel execution where order matters.
     * Note: This simple implementation relies on the caller providing a correct timestamp or logic.
     * For strictly monotonic values like timestamps, we can compare the values themselves if they are comparable.
     * Here we assume the value itself is the timestamp or comparable string for simplicity, 
     * or we rely on the fact that we just want to save the latest execution's result.
     * However, "latest execution" in parallel is ambiguous. 
     * If we want to prevent regression (going back in time), we should compare the value.
     */
    suspend fun updateIfNewer(workflowId: String, moduleId: String, key: String, newValue: String) {
        val currentState = moduleStateDao.getState(workflowId, moduleId, key)
        if (currentState == null) {
            saveState(workflowId, moduleId, key, newValue)
        } else {
            // Compare values as strings (lexicographically) or assume they are timestamps
            // If newValue > currentValue, update.
            if (newValue > currentState.value) {
                 saveState(workflowId, moduleId, key, newValue)
            }
        }
    }
    
    suspend fun deleteStatesForWorkflow(workflowId: String) {
        moduleStateDao.deleteStatesForWorkflow(workflowId)
    }
}
