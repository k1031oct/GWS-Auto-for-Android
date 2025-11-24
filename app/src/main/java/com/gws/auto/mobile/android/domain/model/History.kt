package com.gws.auto.mobile.android.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.gws.auto.mobile.android.data.local.db.DateConverter
import java.util.Date

@Entity(tableName = "execution_history")
@TypeConverters(DateConverter::class)
data class History(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val workflowId: String,
    val workflowName: String,
    val executedAt: Date,
    val status: String, // e.g., "Success", "Failure"
    val logs: String, // Simple text log for now, could be JSON for structured logs
    val durationMs: Long = 0,
    val isBookmarked: Boolean = false,
    val triggerType: String = "MANUAL" // MANUAL or SCHEDULED
)
