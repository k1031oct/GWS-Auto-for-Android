package com.gws.auto.mobile.android.ui.theme;

import com.gws.auto.mobile.android.data.repository.SettingsRepository;
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
public final class ThemeViewModel_Factory implements Factory<ThemeViewModel> {
  private final Provider<SettingsRepository> settingsRepositoryProvider;

  private ThemeViewModel_Factory(Provider<SettingsRepository> settingsRepositoryProvider) {
    this.settingsRepositoryProvider = settingsRepositoryProvider;
  }

  @Override
  public ThemeViewModel get() {
    return newInstance(settingsRepositoryProvider.get());
  }

  public static ThemeViewModel_Factory create(
      Provider<SettingsRepository> settingsRepositoryProvider) {
    return new ThemeViewModel_Factory(settingsRepositoryProvider);
  }

  public static ThemeViewModel newInstance(SettingsRepository settingsRepository) {
    return new ThemeViewModel(settingsRepository);
  }
}
