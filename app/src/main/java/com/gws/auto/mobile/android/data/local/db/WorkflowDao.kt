package com.gws.auto.mobile.android.data.local.db

import androidx.room.*
import com.gws.auto.mobile.android.domain.model.Workflow
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkflowDao {
    @Query("SELECT * FROM workflows ORDER BY `order` ASC")
    fun getAllWorkflows(): Flow<List<Workflow>>

    @Query("SELECT * FROM workflows WHERE id = :id")
    suspend fun getWorkflowById(id: String): Workflow?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkflow(workflow: Workflow)

    @Update
    suspend fun updateWorkflow(workflow: Workflow)

    @Update
    suspend fun updateWorkflows(workflows: List<Workflow>)

    @Delete
    suspend fun deleteWorkflow(workflow: Workflow)
}
