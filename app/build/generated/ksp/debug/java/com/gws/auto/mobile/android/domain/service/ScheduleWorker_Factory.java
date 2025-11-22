package com.gws.auto.mobile.android.domain.service;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.gws.auto.mobile.android.data.repository.ScheduleRepository;
import com.gws.auto.mobile.android.domain.engine.WorkflowEngine;
import dagger.internal.DaggerGenerated;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class ScheduleWorker_Factory {
  private final Provider<WorkflowEngine> workflowEngineProvider;

  private final Provider<ScheduleRepository> scheduleRepositoryProvider;

  private ScheduleWorker_Factory(Provider<WorkflowEngine> workflowEngineProvider,
      Provider<ScheduleRepository> scheduleRepositoryProvider) {
    this.workflowEngineProvider = workflowEngineProvider;
    this.scheduleRepositoryProvider = scheduleRepositoryProvider;
  }

  public ScheduleWorker get(Context appContext, WorkerParameters workerParams) {
    return newInstance(appContext, workerParams, workflowEngineProvider.get(), scheduleRepositoryProvider.get());
  }

  public static ScheduleWorker_Factory create(Provider<WorkflowEngine> workflowEngineProvider,
      Provider<ScheduleRepository> scheduleRepositoryProvider) {
    return new ScheduleWorker_Factory(workflowEngineProvider, scheduleRepositoryProvider);
  }

  public static ScheduleWorker newInstance(Context appContext, WorkerParameters workerParams,
      WorkflowEngine workflowEngine, ScheduleRepository scheduleRepository) {
    return new ScheduleWorker(appContext, workerParams, workflowEngine, scheduleRepository);
  }
}
