package com.gws.auto.mobile.android.ui.settings;

import com.gws.auto.mobile.android.data.repository.SettingsRepository;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;

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
public final class ThemeSettingsFragment_MembersInjector implements MembersInjector<ThemeSettingsFragment> {
  private final Provider<SettingsRepository> settingsRepositoryProvider;

  private ThemeSettingsFragment_MembersInjector(
      Provider<SettingsRepository> settingsRepositoryProvider) {
    this.settingsRepositoryProvider = settingsRepositoryProvider;
  }

  @Override
  public void injectMembers(ThemeSettingsFragment instance) {
    injectSettingsRepository(instance, settingsRepositoryProvider.get());
  }

  public static MembersInjector<ThemeSettingsFragment> create(
      Provider<SettingsRepository> settingsRepositoryProvider) {
    return new ThemeSettingsFragment_MembersInjector(settingsRepositoryProvider);
  }

  @InjectedFieldSignature("com.gws.auto.mobile.android.ui.settings.ThemeSettingsFragment.settingsRepository")
  public static void injectSettingsRepository(ThemeSettingsFragment instance,
      SettingsRepository settingsRepository) {
    instance.settingsRepository = settingsRepository;
  }
}
