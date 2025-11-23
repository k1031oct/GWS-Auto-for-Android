package com.gws.auto.mobile.android.di

import android.content.Context
import androidx.room.Room
import com.gws.auto.mobile.android.data.local.db.AppDatabase
import com.gws.auto.mobile.android.data.local.db.HistoryDao
import com.gws.auto.mobile.android.data.local.db.ScheduleDao
import com.gws.auto.mobile.android.data.local.db.SearchHistoryDao
import com.gws.auto.mobile.android.data.local.db.TagDao
import com.gws.auto.mobile.android.data.local.db.WorkflowDao
import com.gws.auto.mobile.android.data.local.db.WorkflowFolderDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing database-related dependencies, including the main database and DAOs.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).addMigrations(AppDatabase.MIGRATION_6_7)
        .fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideWorkflowDao(appDatabase: AppDatabase): WorkflowDao {
        return appDatabase.workflowDao()
    }

    @Provides
    fun provideHistoryDao(appDatabase: AppDatabase): HistoryDao {
        return appDatabase.historyDao()
    }

    @Provides
    fun provideScheduleDao(appDatabase: AppDatabase): ScheduleDao {
        return appDatabase.scheduleDao()
    }

    @Provides
    fun provideSearchHistoryDao(appDatabase: AppDatabase): SearchHistoryDao {
        return appDatabase.searchHistoryDao()
    }

    @Provides
    fun provideWorkflowFolderDao(appDatabase: AppDatabase): WorkflowFolderDao {
        return appDatabase.workflowFolderDao()
    }

    @Provides
    fun provideTagDao(appDatabase: AppDatabase): TagDao {
        return appDatabase.tagDao()
    }
}
