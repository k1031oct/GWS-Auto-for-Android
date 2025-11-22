package com.gws.auto.mobile.android.di

import com.gws.auto.mobile.android.data.repository.ScheduleRepository
import com.gws.auto.mobile.android.data.repository.ScheduleRepositoryImpl
import com.gws.auto.mobile.android.domain.engine.LocalWorkflowEngine
import com.gws.auto.mobile.android.domain.engine.WorkflowEngine
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for binding repository interfaces to their concrete implementations.
 * Using @Binds is more efficient than @Provides for this purpose.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindScheduleRepository(impl: ScheduleRepositoryImpl): ScheduleRepository

    @Binds
    @Singleton
    abstract fun bindWorkflowEngine(impl: LocalWorkflowEngine): WorkflowEngine
}
