package com.gws.auto.mobile.android.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "module_states", primaryKeys = ["workflowId", "moduleId", "key"])
data class ModuleState(
    val workflowId: String,
    val moduleId: String,
    val key: String,
    val value: String,
    val updatedAt: Long = System.currentTimeMillis()
)
