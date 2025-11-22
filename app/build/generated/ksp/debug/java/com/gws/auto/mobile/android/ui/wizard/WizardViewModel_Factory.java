package com.gws.auto.mobile.android.ui.wizard;

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
public final class WizardViewModel_Factory implements Factory<WizardViewModel> {
  private final Provider<SettingsRepository> settingsRepositoryProvider;

  private WizardViewModel_Factory(Provider<SettingsRepository> settingsRepositoryProvider) {
    this.settingsRepositoryProvider = settingsRepositoryProvider;
  }

  @Override
  public WizardViewModel get() {
    return newInstance(settingsRepositoryProvider.get());
  }

  public static WizardViewModel_Factory create(
      Provider<SettingsRepository> settingsRepositoryProvider) {
    return new WizardViewModel_Factory(settingsRepositoryProvider);
  }

  public static WizardViewModel newInstance(SettingsRepository settingsRepository) {
    return new WizardViewModel(settingsRepository);
  }
}
