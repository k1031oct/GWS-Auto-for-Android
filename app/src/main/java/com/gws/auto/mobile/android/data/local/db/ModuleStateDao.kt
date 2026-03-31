package com.gws.auto.mobile.android.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gws.auto.mobile.android.domain.model.ModuleState

@Dao
interface ModuleStateDao {
    @Query("SELECT * FROM module_states WHERE workflowId = :workflowId AND moduleId = :moduleId AND `key` = :key")
    suspend fun getState(workflowId: String, moduleId: String, key: String): ModuleState?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertState(state: ModuleState)

    @Query("DELETE FROM module_states WHERE workflowId = :workflowId")
    suspend fun deleteStatesForWorkflow(workflowId: String)
}
