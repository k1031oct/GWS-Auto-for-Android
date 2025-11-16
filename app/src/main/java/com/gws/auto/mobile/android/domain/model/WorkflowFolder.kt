package com.gws.auto.mobile.android.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.gws.auto.mobile.android.data.local.db.ListConverter

@Entity(tableName = "workflow_folders")
@TypeConverters(ListConverter::class)
data class WorkflowFolder(
    @PrimaryKey
    val id: String,
    val name: String,
    val workflowIds: List<String>
)
