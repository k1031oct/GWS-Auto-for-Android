package com.gws.auto.mobile.android.ui.schedule;

import android.content.Context;
import androidx.lifecycle.SavedStateHandle;
import com.gws.auto.mobile.android.data.repository.ScheduleRepository;
import com.gws.auto.mobile.android.data.repository.SettingsRepository;
import com.gws.auto.mobile.android.data.repository.WorkflowRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class ScheduleSettingsViewModel_Factory implements Factory<ScheduleSettingsViewModel> {
  private final Provider<Context> contextProvider;

  private final Provider<ScheduleRepository> scheduleRepositoryProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  private final Provider<WorkflowRepository> workflowRepositoryProvider;

  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private ScheduleSettingsViewModel_Factory(Provider<Context> contextProvider,
      Provider<ScheduleRepository> scheduleRepositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<WorkflowRepository> workflowRepositoryProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    this.contextProvider = contextProvider;
    this.scheduleRepositoryProvider = scheduleRepositoryProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
    this.workflowRepositoryProvider = workflowRepositoryProvider;
    this.savedStateHandleProvider = savedStateHandleProvider;
  }

  @Override
  public ScheduleSettingsViewModel get() {
    return newInstance(contextProvider.get(), scheduleRepositoryProvider.get(), settingsRepositoryProvider.get(), workflowRepositoryProvider.get(), savedStateHandleProvider.get());
  }

  public static ScheduleSettingsViewModel_Factory create(Provider<Context> contextProvider,
      Provider<ScheduleRepository> scheduleRepositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<WorkflowRepository> workflowRepositoryProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    return new ScheduleSettingsViewModel_Factory(contextProvider, scheduleRepositoryProvider, settingsRepositoryProvider, workflowRepositoryProvider, savedStateHandleProvider);
  }

  public static ScheduleSettingsViewModel newInstance(Context context,
      ScheduleRepository scheduleRepository, SettingsRepository settingsRepository,
      WorkflowRepository workflowRepository, SavedStateHandle savedStateHandle) {
    return new ScheduleSettingsViewModel(context, scheduleRepository, settingsRepository, workflowRepository, savedStateHandle);
  }
}
