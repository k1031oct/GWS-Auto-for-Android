package com.gws.auto.mobile.android.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gws.auto.mobile.android.domain.model.History
import com.gws.auto.mobile.android.domain.model.Module
import com.gws.auto.mobile.android.domain.model.SearchHistory
import com.gws.auto.mobile.android.domain.model.Tag
import com.gws.auto.mobile.android.domain.model.Workflow
import com.gws.auto.mobile.android.domain.model.WorkflowFolder

@Database(
    entities = [Workflow::class, Module::class, SearchHistory::class, WorkflowFolder::class, Tag::class, History::class],
    version = 4,
    exportSchema = false
)
@TypeConverters(DateConverter::class, ListConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun workflowDao(): WorkflowDao
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun workflowFolderDao(): WorkflowFolderDao
    abstract fun tagDao(): TagDao
    abstract fun historyDao(): HistoryDao

    companion object {
        const val DATABASE_NAME = "gws-auto-db"
    }
}
