package com.gws.auto.mobile.android.domain.service;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.gws.auto.mobile.android.data.repository.ScheduleRepository;
import com.gws.auto.mobile.android.data.repository.SettingsRepository;
import com.gws.auto.mobile.android.domain.engine.WorkflowEngine;
import com.gws.auto.mobile.android.domain.notification.NotificationHelper;
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

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  private final Provider<NotificationHelper> notificationHelperProvider;

  private ScheduleWorker_Factory(Provider<WorkflowEngine> workflowEngineProvider,
      Provider<ScheduleRepository> scheduleRepositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<NotificationHelper> notificationHelperProvider) {
    this.workflowEngineProvider = workflowEngineProvider;
    this.scheduleRepositoryProvider = scheduleRepositoryProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
    this.notificationHelperProvider = notificationHelperProvider;
  }

  public ScheduleWorker get(Context appContext, WorkerParameters workerParams) {
    return newInstance(appContext, workerParams, workflowEngineProvider.get(), scheduleRepositoryProvider.get(), settingsRepositoryProvider.get(), notificationHelperProvider.get());
  }

  public static ScheduleWorker_Factory create(Provider<WorkflowEngine> workflowEngineProvider,
      Provider<ScheduleRepository> scheduleRepositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<NotificationHelper> notificationHelperProvider) {
    return new ScheduleWorker_Factory(workflowEngineProvider, scheduleRepositoryProvider, settingsRepositoryProvider, notificationHelperProvider);
  }

  public static ScheduleWorker newInstance(Context appContext, WorkerParameters workerParams,
      WorkflowEngine workflowEngine, ScheduleRepository scheduleRepository,
      SettingsRepository settingsRepository, NotificationHelper notificationHelper) {
    return new ScheduleWorker(appContext, workerParams, workflowEngine, scheduleRepository, settingsRepository, notificationHelper);
  }
}
