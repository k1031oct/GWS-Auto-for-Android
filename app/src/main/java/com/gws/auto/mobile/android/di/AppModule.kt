package com.gws.auto.mobile.android.di

import android.content.Context
import androidx.room.Room
import com.gws.auto.mobile.android.data.local.db.AppDatabase
import com.gws.auto.mobile.android.data.local.db.SearchHistoryDao
import com.gws.auto.mobile.android.data.local.db.WorkflowDao
import com.gws.auto.mobile.android.data.local.db.WorkflowFolderDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    fun provideWorkflowDao(appDatabase: AppDatabase): WorkflowDao {
        return appDatabase.workflowDao()
    }

    @Provides
    fun provideSearchHistoryDao(appDatabase: AppDatabase): SearchHistoryDao {
        return appDatabase.searchHistoryDao()
    }

    @Provides
    fun provideWorkflowFolderDao(appDatabase: AppDatabase): WorkflowFolderDao {
        return appDatabase.workflowFolderDao()
    }
}
