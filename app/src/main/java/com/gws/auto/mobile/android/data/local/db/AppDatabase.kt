package com.gws.auto.mobile.android.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.gws.auto.mobile.android.domain.model.History
import com.gws.auto.mobile.android.domain.model.Module
import com.gws.auto.mobile.android.domain.model.ModuleState
import com.gws.auto.mobile.android.domain.model.Schedule
import com.gws.auto.mobile.android.domain.model.SearchHistory
import com.gws.auto.mobile.android.domain.model.Tag
import com.gws.auto.mobile.android.domain.model.Workflow
import com.gws.auto.mobile.android.domain.model.WorkflowFolder

@Database(
    entities = [Workflow::class, Module::class, SearchHistory::class, WorkflowFolder::class, Tag::class, History::class, Schedule::class, ModuleState::class],
    version = 10,
    exportSchema = false
)
@TypeConverters(DateConverter::class, ListConverter::class, ScheduleTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun workflowDao(): WorkflowDao
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun workflowFolderDao(): WorkflowFolderDao
    abstract fun tagDao(): TagDao
    abstract fun historyDao(): HistoryDao
    abstract fun scheduleDao(): ScheduleDao
    abstract fun moduleStateDao(): ModuleStateDao

    companion object {
        const val DATABASE_NAME = "gws-auto-db"

        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE schedules ADD COLUMN skipHolidays INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE execution_history ADD COLUMN triggerType TEXT NOT NULL DEFAULT 'MANUAL'")
            }
        }

        val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE workflows ADD COLUMN `order` INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `module_states` (`workflowId` TEXT NOT NULL, `moduleId` TEXT NOT NULL, `key` TEXT NOT NULL, `value` TEXT NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`workflowId`, `moduleId`, `key`))")
            }
        }
    }
}
