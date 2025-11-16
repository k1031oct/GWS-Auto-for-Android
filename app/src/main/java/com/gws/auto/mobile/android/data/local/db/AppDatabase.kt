package com.gws.auto.mobile.android.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gws.auto.mobile.android.domain.model.Module
import com.gws.auto.mobile.android.domain.model.SearchHistory
import com.gws.auto.mobile.android.domain.model.Workflow
import com.gws.auto.mobile.android.domain.model.WorkflowFolder

@Database(entities = [Workflow::class, Module::class, SearchHistory::class, WorkflowFolder::class], version = 2)
abstract class AppDatabase : RoomDatabase() {

    abstract fun workflowDao(): WorkflowDao
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun workflowFolderDao(): WorkflowFolderDao

    companion object {
        const val DATABASE_NAME = "gws-auto-db"
    }
}
