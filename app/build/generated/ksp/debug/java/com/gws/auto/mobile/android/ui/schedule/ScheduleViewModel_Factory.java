package com.gws.auto.mobile.android.ui.schedule;

import com.gws.auto.mobile.android.data.repository.ScheduleRepository;
import com.gws.auto.mobile.android.data.repository.SettingsRepository;
import com.gws.auto.mobile.android.domain.service.GoogleApiAuthorizer;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class ScheduleViewModel_Factory implements Factory<ScheduleViewModel> {
  private final Provider<ScheduleRepository> scheduleRepositoryProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  private final Provider<GoogleApiAuthorizer> googleApiAuthorizerProvider;

  private ScheduleViewModel_Factory(Provider<ScheduleRepository> scheduleRepositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<GoogleApiAuthorizer> googleApiAuthorizerProvider) {
    this.scheduleRepositoryProvider = scheduleRepositoryProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
    this.googleApiAuthorizerProvider = googleApiAuthorizerProvider;
  }

  @Override
  public ScheduleViewModel get() {
    return newInstance(scheduleRepositoryProvider.get(), settingsRepositoryProvider.get(), googleApiAuthorizerProvider.get());
  }

  public static ScheduleViewModel_Factory create(
      Provider<ScheduleRepository> scheduleRepositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<GoogleApiAuthorizer> googleApiAuthorizerProvider) {
    return new ScheduleViewModel_Factory(scheduleRepositoryProvider, settingsRepositoryProvider, googleApiAuthorizerProvider);
  }

  public static ScheduleViewModel newInstance(ScheduleRepository scheduleRepository,
      SettingsRepository settingsRepository, GoogleApiAuthorizer googleApiAuthorizer) {
    return new ScheduleViewModel(scheduleRepository, settingsRepository, googleApiAuthorizer);
  }
}
