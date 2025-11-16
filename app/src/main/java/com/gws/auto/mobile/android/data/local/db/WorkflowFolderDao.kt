package com.gws.auto.mobile.android.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gws.auto.mobile.android.domain.model.WorkflowFolder
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkflowFolderDao {

    @Query("SELECT * FROM workflowfolder")
    fun getAllWorkflowFolders(): Flow<List<WorkflowFolder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkflowFolder(folder: WorkflowFolder)

    @Update
    suspend fun updateWorkflowFolder(folder: WorkflowFolder)

    @Query("DELETE FROM workflowfolder WHERE id = :folderId")
    suspend fun deleteWorkflowFolder(folderId: String)
}
