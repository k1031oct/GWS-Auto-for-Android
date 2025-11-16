package com.gws.auto.mobile.android.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.gws.auto.mobile.android.data.local.db.MapConverter

/**
 * A single executable task within a workflow.
 */
@Entity(tableName = "modules")
@TypeConverters(MapConverter::class)
data class Module(
    @PrimaryKey
    val id: String,
    val type: String, // e.g., "CREATE_PDF_FROM_SHEET"
    val parameters: Map<String, String> // e.g., {"sheet_url": "https://...", "output_name": "report.pdf"}
)
