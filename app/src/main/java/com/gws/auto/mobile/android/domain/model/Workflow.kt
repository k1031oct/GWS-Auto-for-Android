package com.gws.auto.mobile.android.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.gws.auto.mobile.android.data.local.db.ListConverter
import com.gws.auto.mobile.android.data.local.db.ModuleListConverter

/**
 * A collection of modules to be executed sequentially.
 */
@Entity(tableName = "workflows")
@TypeConverters(ModuleListConverter::class, ListConverter::class)
data class Workflow(
    @PrimaryKey
    val id: String,
    val name: String = "",
    val description: String = "",
    val modules: List<Module> = emptyList(),
    val status: String = "",
    val trigger: String = "",
    val tags: List<String> = emptyList(),
    val isFavorite: Boolean = false,
    val order: Int = 0
)
