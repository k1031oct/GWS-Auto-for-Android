package com.gws.auto.mobile.android.ui.settings.app;

import com.gws.auto.mobile.android.data.repository.HistoryRepository;
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
public final class AppSettingsFragment_MembersInjector implements MembersInjector<AppSettingsFragment> {
  private final Provider<SettingsRepository> settingsRepositoryProvider;

  private final Provider<HistoryRepository> historyRepositoryProvider;

  private AppSettingsFragment_MembersInjector(
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<HistoryRepository> historyRepositoryProvider) {
    this.settingsRepositoryProvider = settingsRepositoryProvider;
    this.historyRepositoryProvider = historyRepositoryProvider;
  }

  @Override
  public void injectMembers(AppSettingsFragment instance) {
    injectSettingsRepository(instance, settingsRepositoryProvider.get());
    injectHistoryRepository(instance, historyRepositoryProvider.get());
  }

  public static MembersInjector<AppSettingsFragment> create(
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<HistoryRepository> historyRepositoryProvider) {
    return new AppSettingsFragment_MembersInjector(settingsRepositoryProvider, historyRepositoryProvider);
  }

  @InjectedFieldSignature("com.gws.auto.mobile.android.ui.settings.app.AppSettingsFragment.settingsRepository")
  public static void injectSettingsRepository(AppSettingsFragment instance,
      SettingsRepository settingsRepository) {
    instance.settingsRepository = settingsRepository;
  }

  @InjectedFieldSignature("com.gws.auto.mobile.android.ui.settings.app.AppSettingsFragment.historyRepository")
  public static void injectHistoryRepository(AppSettingsFragment instance,
      HistoryRepository historyRepository) {
    instance.historyRepository = historyRepository;
  }
}
